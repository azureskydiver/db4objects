using System;
using System.Text;
using Db4objects.Db4o.Tests.Common.Handlers;
using Db4oUnit;

namespace Db4objects.Db4o.Tests.CLI1.Handlers
{
    class ULongHandlerUpdateTestCase : HandlerUpdateTestCaseBase
    {
        public class Item
        {
            public ulong _typedPrimitive;

            public object _untyped;

#if NET_2_0 || CF_2_0
			public ulong? _nullablePrimitive;
#endif
        }

        public class ItemArrays
        {
            public ulong[] _typedPrimitiveArray;

            public object _primitiveArrayInObject;
#if NET_2_0 || CF_2_0
            public ulong?[] _nullableTypedPrimitiveArray;
#endif
        }

        private static readonly ulong[] data = new ulong[] {
            ulong.MinValue,
            ulong.MinValue + 1,
            5,
            ulong.MaxValue - 1,
            ulong.MaxValue,
        };

        protected override void AssertArrays(object obj)
        {
            ItemArrays itemArrays = (ItemArrays)obj;
            for (int i = 0; i < data.Length; i++)
            {
                AssertAreEqual(data[i], itemArrays._typedPrimitiveArray[i]);
                AssertAreEqual(data[i], ((ulong[])itemArrays._primitiveArrayInObject)[i]);
                //AssertAreEqual(data[i], (ulong)itemArrays._nullableTypedPrimitiveArray[i]);
            }
            AssertAreEqual(0, itemArrays._typedPrimitiveArray[data.Length]);
            AssertAreEqual(0, ((ulong[])itemArrays._primitiveArrayInObject)[data.Length]);
        }

        protected override void AssertValues(object[] values)
        {
            for (int i = 0; i < data.Length; i++)
            {
                Item item = (Item)values[i];
                AssertAreEqual(data[i], item._typedPrimitive);
                AssertAreEqual(data[i], (ulong) item._untyped);
#if NET_2_0 || CF_2_0
                AssertAreEqual(data[i], (ulong) item._nullablePrimitive);
#endif
				}
            Item nullItem = (Item)values[data.Length];
            AssertAreEqual(0, nullItem._typedPrimitive);
            Assert.IsNull(nullItem._untyped);
#if NET_2_0 || CF_2_0
            Assert.IsNull(nullItem._nullablePrimitive);
#endif
			}

        private void AssertAreEqual(ulong expected, ulong actual)
        {
            Assert.AreEqual(expected, actual);
        }

        protected override object CreateArrays()
        {
            ItemArrays itemArrays = new ItemArrays();
            itemArrays._typedPrimitiveArray = new ulong[data.Length + 1];
            System.Array.Copy(data, 0, itemArrays._typedPrimitiveArray, 0, data.Length);

            ulong[] ulongArray = new ulong[data.Length + 1];
            System.Array.Copy(data, 0, ulongArray, 0, data.Length);
            itemArrays._primitiveArrayInObject = ulongArray;

#if NET_2_0 || CF_2_0
            itemArrays._nullableTypedPrimitiveArray = new ulong?[data.Length + 1];
            for (int i = 0; i < data.Length; i++)
            {
                itemArrays._nullableTypedPrimitiveArray[i] = data[i];
            }
#endif
			return itemArrays;
        }

        protected override object[] CreateValues()
        {
            Item[] values = new Item[data.Length + 1];
            for (int i = 0; i < data.Length; i++)
            {
                Item item = new Item();
                item._typedPrimitive = data[i];
                item._untyped = data[i];
#if NET_2_0 || CF_2_0
                item._nullablePrimitive = data[i];
#endif
				values[i] = item;
            }

            values[data.Length] = new Item();
            return values;
        }

        protected override string TypeName()
        {
            return "ulong";
        }
    }

}
