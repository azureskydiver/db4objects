package com.db4o.ta.instrumentation;

import EDU.purdue.cs.bloat.editor.ClassEditor;
import EDU.purdue.cs.bloat.editor.EditorVisitor;
import EDU.purdue.cs.bloat.editor.FieldEditor;
import EDU.purdue.cs.bloat.editor.Instruction;
import EDU.purdue.cs.bloat.editor.Label;
import EDU.purdue.cs.bloat.editor.LocalVariable;
import EDU.purdue.cs.bloat.editor.MemberRef;
import EDU.purdue.cs.bloat.editor.MethodEditor;
import EDU.purdue.cs.bloat.editor.NameAndType;
import EDU.purdue.cs.bloat.editor.Opcode;
import EDU.purdue.cs.bloat.editor.Type;
import EDU.purdue.cs.bloat.reflect.Modifiers;

import com.db4o.activation.Activator;
import com.db4o.instrumentation.BloatClassEdit;
import com.db4o.instrumentation.ClassFilter;
import com.db4o.ta.Activatable;

public class InjectTransparentActivationEdit implements BloatClassEdit {

	private final LocalVariable THIS_VAR = new LocalVariable(0);
	private final ClassFilter _instrumentedClassesFilter;
	
	public InjectTransparentActivationEdit(ClassFilter instrumentedClassesFilter) {
		_instrumentedClassesFilter = instrumentedClassesFilter;
	}
	
	public void bloat(ClassEditor ce) {
		String superClassName = normalizeClassName(ce.superclass().className());
		if(!(_instrumentedClassesFilter.accept(superClassName))) {
			ce.addInterface(Activatable.class);
			createActivatorField(ce);
			createBindMethod(ce);
			createActivateMethod(ce);
		}
		instrumentAllMethods(ce);
	}

	private void createActivatorField(ClassEditor ce) {
		// private transient Activator _activator;
		FieldEditor fieldEditor = new FieldEditor(ce, Modifiers.PRIVATE | Modifiers.TRANSIENT, Type.getType(Activator.class), TransparentActivationInstrumentationConstants.ACTIVATOR_FIELD_NAME);
		fieldEditor.commit();
	}

	private void createBindMethod(ClassEditor ce) {
		// public void bind(Activator activator)
		final Type activatorType = Type.getType(Activator.class);
		String methodName = TransparentActivationInstrumentationConstants.BIND_METHOD_NAME;
		Type[] paramTypes = { activatorType };
		MethodEditor methodEditor = new MethodEditor(ce, Modifiers.PUBLIC, Type.VOID, methodName, paramTypes, new Type[] {});
		Label startLabel = new Label(0);
		Label setActivatorLabel = new Label(1);
		LocalVariable activatorArg = new LocalVariable(1);
		
		methodEditor.addLabel(startLabel);

		// if (null != _activator) 
		loadActivatorFieldOnStack(methodEditor);
		methodEditor.addInstruction(Opcode.opc_ifnull, setActivatorLabel);
		
		// throw new IllegalStateException();
		throwException(methodEditor, IllegalStateException.class);
		
		methodEditor.addLabel(setActivatorLabel);
		
		// _activator = activator;
		loadThisOnStack(methodEditor);
		methodEditor.addInstruction(Opcode.opc_aload, activatorArg);
		methodEditor.addInstruction(Opcode.opc_putfield, createFieldReference(ce.type(), TransparentActivationInstrumentationConstants.ACTIVATOR_FIELD_NAME, activatorType));		
		methodEditor.addInstruction(Opcode.opc_return);
		
		methodEditor.commit();
	}

	private void throwException(MethodEditor methodEditor, Class exceptionType) {
		Type illegalStateExceptionType = Type.getType(exceptionType);
		methodEditor.addInstruction(Opcode.opc_new, illegalStateExceptionType);
		methodEditor.addInstruction(Opcode.opc_dup);
		methodEditor.addInstruction(Opcode.opc_invokespecial, createMethodReference(illegalStateExceptionType, TransparentActivationInstrumentationConstants.INIT_METHOD_NAME, new Type[0], Type.VOID));

		methodEditor.addInstruction(Opcode.opcx_athrow);
	}

	private void createActivateMethod(ClassEditor ce) {
		// protected void activate()
		final Type activatorType = Type.getType(Activator.class);
		String methodName = TransparentActivationInstrumentationConstants.ACTIVATE_METHOD_NAME;
		MethodEditor methodEditor = new MethodEditor(ce, Modifiers.PROTECTED, Type.VOID, methodName, new Type[] { }, new Type[] {});
		Label startLabel = new Label(0);
		Label activateLabel = new Label(1);

		// if (_activator == null) { return; }
		methodEditor.addLabel(startLabel);
		loadActivatorFieldOnStack(methodEditor);
		methodEditor.addInstruction(Opcode.opc_ifnonnull, activateLabel);
		methodEditor.addInstruction(Opcode.opc_return);
		
		// _activator.activate();
		methodEditor.addLabel(activateLabel);
		loadActivatorFieldOnStack(methodEditor);
		methodEditor.addInstruction(Opcode.opc_invokeinterface, createMethodReference(activatorType, TransparentActivationInstrumentationConstants.ACTIVATOR_ACTIVATE_METHOD_NAME, new Type[] { }, Type.VOID));
		methodEditor.addInstruction(Opcode.opc_return);
		
		methodEditor.commit();
	}

	private void instrumentAllMethods(final ClassEditor ce) {
		final MemberRef activateMethod = createMethodReference(ce.type(), TransparentActivationInstrumentationConstants.ACTIVATE_METHOD_NAME, new Type[]{}, Type.VOID);
		final MemberRef bindMethod = createMethodReference(ce.type(), TransparentActivationInstrumentationConstants.BIND_METHOD_NAME, new Type[]{ Type.getType(Activator.class) }, Type.VOID);
		ce.visit(new EditorVisitor() {

			public void visitClassEditor(ClassEditor editor) {
			}

			public void visitFieldEditor(FieldEditor editor) {
			}

			public void visitMethodEditor(MethodEditor editor) {
				if(editor.isConstructor()) {
					return;
				}
				MemberRef methodRef = editor.memberRef();
				if(methodRef.equals(activateMethod) || methodRef.equals(bindMethod)) {
					return;
				}
				// activate();
				editor.insertCodeAt(new Instruction(Opcode.opc_aload, new LocalVariable(0)), 1);
				editor.insertCodeAt(new Instruction(Opcode.opc_invokevirtual, createMethodReference(ce.type(), TransparentActivationInstrumentationConstants.ACTIVATE_METHOD_NAME, new Type[]{}, Type.VOID)), 2);
				editor.commit();
			}
			
		});
	}

	private void loadThisOnStack(MethodEditor methodEditor) {
		methodEditor.addInstruction(Opcode.opc_aload, THIS_VAR);
	}
	
	private void loadActivatorFieldOnStack(MethodEditor methodEditor) {
		Type activatorType = Type.getType(Activator.class);
		loadThisOnStack(methodEditor);
		methodEditor.addInstruction(Opcode.opc_getfield, createFieldReference(methodEditor.declaringClass().type(), TransparentActivationInstrumentationConstants.ACTIVATOR_FIELD_NAME, activatorType));
	}
	
	private MemberRef createMethodReference(Type parent, String name, Type[] args, Type ret) {
		NameAndType nameAndType = new NameAndType(name, Type.getType(args, ret));
		return new MemberRef(parent, nameAndType);
	}

	private MemberRef createFieldReference(Type parent, String name, Type type) {
		NameAndType nameAndType = new NameAndType(name, type);
		return new MemberRef(parent, nameAndType);
	}

	private String normalizeClassName(String className) {
		return className.replace('/', '.');
	}
}
