using System;
using System.Diagnostics;
using com.db4o.inside.query;
using com.db4o.nativequery.expr;
using com.db4o.query;
using Db4oTools.NativeQueries;
using Mono.Cecil;
using Mono.Cecil.Cil;
using MethodAttributes=Mono.Cecil.MethodAttributes;

namespace Db4oAdmin
{
	public class PredicateOptimizer : AbstractAssemblyInstrumentation
	{
		protected override void ProcessType(TypeDefinition type)
		{
			if (IsPredicateClass(type))
			{
				InstrumentPredicateClass(type);
			}
			base.ProcessType(type);
		}

		private void InstrumentPredicateClass(TypeDefinition type)
		{
			MethodDefinition match = GetMatchMethod(type);
			Expression e = GetExpression(match);
			if (null == e) return;

			OptimizePredicate(type, match, e);
		}

		private void OptimizePredicate(TypeDefinition type, MethodDefinition match, Expression e)
		{
			MethodDefinition optimizeQuery = CreateOptimizeQueryMethod();

			TypeReference extent = match.Parameters[0].ParameterType;
			EmitPrologue(optimizeQuery, extent);

			e.Accept(new SodaEmitterVisitor(_context, optimizeQuery));

			EmitEpilogue(optimizeQuery);

			type.Methods.Add(optimizeQuery);
			type.Interfaces.Add(_context.Import(typeof(Db4oEnhancedFilter)));
		}

		private static Expression GetExpression(MethodDefinition match)
		{
			try
			{
				return QueryExpressionBuilder.FromMethodDefinition(match);
			}
			catch (UnsupportedPredicateException x)
			{	
				Console.Error.WriteLine("WARNING: Predicate '{0}' could not be optimized. {1}", match.DeclaringType, x.Message);
			}
			return null;
		}

		private static void EmitEpilogue(MethodDefinition method)
		{
			method.Body.CilWorker.Emit(OpCodes.Ret);
		}

		private void EmitPrologue(MethodDefinition method, TypeReference extent)
		{
			CilWorker worker = method.Body.CilWorker;
			
			// query.Constrain(extent);
			worker.Emit(OpCodes.Ldarg_1);
			worker.Emit(OpCodes.Ldtoken, extent);
			worker.Emit(OpCodes.Call, _context.Import(typeof(Type).GetMethod("GetTypeFromHandle")));
			worker.Emit(OpCodes.Callvirt, _context.Import(typeof(Query).GetMethod("Constrain")));
			worker.Emit(OpCodes.Pop);
		}

		private MethodDefinition CreateOptimizeQueryMethod()
		{
			// TODO: make sure importing typeof(void) is ok here for the
			// following scenario: CF 1.0 assembly being instrumented by
			// Db4oAdmin running under .net 2.0
			MethodDefinition method = new MethodDefinition("OptimizeQuery",
			                                               MethodAttributes.Virtual|MethodAttributes.Public,
														   _context.Import(typeof(void)));
			method.Parameters.Add(new ParameterDefinition(_context.Import(typeof(Query))));
			
			return method;
		}

		private MethodDefinition GetMatchMethod(TypeDefinition type)
		{
			MethodDefinition[] methods = type.Methods.GetMethod("Match");
			Debug.Assert(1 == methods.Length);
			return methods[0];
		}

		private bool IsPredicateClass(TypeReference typeRef)
		{
			TypeDefinition type = typeRef as TypeDefinition;
			if (null == type) return false;
			TypeReference baseType = type.BaseType;
			if (null == baseType) return false;
			if ("com.db4o.query.Predicate" == baseType.FullName) return true;
			return IsPredicateClass(baseType);
		}
	}
}
