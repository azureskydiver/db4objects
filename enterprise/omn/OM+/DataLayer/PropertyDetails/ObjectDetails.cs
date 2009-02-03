using System;
using System.Collections.Generic;
using Db4objects.Db4o; 
using Db4objects.Db4o.Ext ;
using Db4objects.Db4o.Reflect;
using Db4objects.Db4o.Reflect.Generic;
using OManager.DataLayer.CommonDatalayer;
using OManager.DataLayer.Connection;
using OME.Logging.Common;
using System.Collections;
using OManager.BusinessLayer.Common;

namespace OManager.DataLayer.Modal
{
    public class ObjectDetails 
    {
        private readonly IObjectContainer objectContainer;

		int level;
        private readonly object m_genObject;
		private readonly List<object> m_listforObjects = new List<object>();

        public ObjectDetails(object genObject)
        {
            m_genObject = genObject;
            objectContainer = Db4oClient.Client;
        }
        public string GetUUID()
        {
            try
            {
                IObjectInfo objInfo = objectContainer.Ext().GetObjectInfo(m_genObject);
                if (objInfo != null)
                {
                    Db4oUUID uuid = objInfo.GetUUID() ;
                    return uuid != null ? uuid.GetLongPart().ToString() : "NA";
                }
            	
				return "NA";
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return "NA";
            }
        }

        public long GetLocalID()
        {
            try
            {
                IObjectInfo objInfo = objectContainer.Ext().GetObjectInfo(m_genObject);
				return objInfo != null ? objInfo.GetInternalID() : 0;
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return 0;
            }
        }

        public int GetDepth(object obj)
        {
            try{
            level++;
            IReflectClass rclass = DataLayerCommon.ReflectClassFor(obj);
                if (rclass != null)
                {
                    IReflectField[] fieldArr = DataLayerCommon.GetDeclaredFieldsInHeirarchy(rclass);
                    if (fieldArr != null)
                    {
                        foreach (IReflectField field in fieldArr)
                        {

                            object getObject = field.Get(obj);
                            string fieldType = field.GetFieldType().GetName();
                            fieldType = DataLayerCommon.PrimitiveType(fieldType);
                            if (getObject != null)
                            {
                                if (!CommonValues.IsPrimitive(fieldType))
                                {
                                    if (field.GetFieldType().IsCollection())
                                    {
                                        ICollection coll = (ICollection)field.Get(obj);
                                        ArrayList arrList = new ArrayList(coll);

                                        for (int i = 0; i < arrList.Count; i++)
                                        {
                                            object colObject = arrList[i];
                                            if (colObject != null)
                                            {
                                                if (colObject is GenericObject)
                                                {
                                                    if (!m_listforObjects.Contains(colObject))
                                                    {
                                                        m_listforObjects.Add(colObject);
                                                        level = GetDepth(colObject);
                                                       
                                                    }
                                                }
                                            }
                                        }
                                       
                                       
                                    }
                                    else if (field.GetFieldType().IsArray())
                                    {

                                        int length = objectContainer.Ext().Reflector().Array().GetLength(field.Get(obj));
                                        for (int i = 0; i < length; i++)
                                        {
                                            object arrObject = objectContainer.Ext().Reflector().Array().Get(field.Get(obj), i);
                                            if (arrObject != null)
                                            {
                                                if (arrObject is GenericObject)
                                                {
                                                    if (!m_listforObjects.Contains(arrObject))
                                                    {
                                                        m_listforObjects.Add(arrObject);
                                                        level=GetDepth(arrObject);
                                                      
                                                    }
                                                }

                                            }
                                        }
                                    }
                                    else
                                    {
                                        if (!m_listforObjects.Contains(getObject))
                                        {
                                            m_listforObjects.Add(getObject);
                                             level=GetDepth(getObject);
                                           
                                        }

                                    }
                                }

                            }
                        }
                       
                    }
                }
            }
            catch (Exception oEx)
            {
               
                LoggingHelper.HandleException(oEx);

            }

            return level;
        }

        public object GetObjById(long id)
        {
            IObjectContainer objectContainer = Db4oClient.Client;
            return objectContainer.Ext().GetByID(id);
        }

        public bool checkReference(int objectId)
        {
            //not clear how to implemet now will be discussed later.
            return false ;
        }

        public long GetVersion()
        {
            const long version = 0;
            try
            {
            	IObjectInfo objInfo = objectContainer.Ext().GetObjectInfo(m_genObject);
            	return objInfo != null ? objInfo.GetVersion() : version;
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return 0;
            }
        }
    }
}
