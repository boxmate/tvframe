/**
 * Copyright (c) 2012-2013, Michael Yang ï¿½ï¿½??§ï¿½ï¿½å¨´ï¿? (www.yangfuhai.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reco.frame.tv.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import reco.frame.tv.annotation.sqlite.Id;
import reco.frame.tv.annotation.sqlite.Table;
import reco.frame.tv.db.sqlite.ManyToOneLazyLoader;
import reco.frame.tv.db.table.ManyToOne;
import reco.frame.tv.db.table.OneToMany;
import reco.frame.tv.db.table.Property;
import reco.frame.tv.exception.DbException;

public class ClassUtils {
	
	
	/**
	 * ¸ù¾ÝÊµÌåÀà »ñµÃ ÊµÌåÀà¶ÔÓ¦µÄ±íÃû
	 * @param entity
	 * @return
	 */
	public static String getTableName(Class<?> clazz) {
		Table table = clazz.getAnnotation(Table.class);
		if(table == null || table.name().trim().length() == 0 ){
			//è¤°ï¿½å¨????ï¿½ï¿½å¨???¨Ð?ï¿½ï¿½ï¿½ï¿½ï¿½è·ºï¿½ï¿½æ¦?ï¿½ç?????ï¿½ã?§è??ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ç»?é¢?ï¿½ï¿½æ¶?é¸¿ã??ï¿½ï¿½ï¿?,éª???µï¿½ï¿½ï¿½ï¿½ç?¸ï¿½ï¿?.???ï¿½ï¿½ï¿½æ??ï¿½ï¿½æ¶?è½°ï¿½ï¿½ï¿½ï¿½ï¿½ç»¾ï¿½(_)
			return clazz.getName().replace('.', '_');
		}
		return table.name();
	}
	
	public static Object getPrimaryKeyValue(Object entity) {
		return FieldUtils.getFieldValue(entity,ClassUtils.getPrimaryKeyField(entity.getClass()));
	}
	
	/**
	 * ¸ù¾ÝÊµÌåÀà »ñµÃ ÊµÌåÀà¶ÔÓ¦µÄ±íÃû
	 * @param entity
	 * @return
	 */
	public static String getPrimaryKeyColumn(Class<?> clazz) {
		String primaryKey = null ;
		Field[] fields = clazz.getDeclaredFields();
		if(fields != null){
			Id idAnnotation = null ;
			Field idField = null ;
			
			for(Field field : fields){ //ï¿½ï¿½å³°ï¿½ï¿?IDå¨???¨Ð?
				idAnnotation = field.getAnnotation(Id.class);
				if(idAnnotation != null){
					idField = field;
					break;
				}
			}
			
			if(idAnnotation != null){ //ï¿½ï¿½ï¿?IDå¨???¨Ð?
				primaryKey = idAnnotation.column();
				if(primaryKey == null || primaryKey.trim().length() == 0)
					primaryKey = idField.getName();
			}else{ //å¨????ï¿½ï¿½IDå¨???¨Ð?,æ¦?ï¿½ç?????ï¿½ç??ï¿½ï¿½ _id ï¿½ï¿½ï¿? id æ¶?è½°å??ï¿½ï¿½ï¿½é??ï¿½æµ¼ï¿½ï¿½ï¿½ï¿½??µç??ï¿½ï¿½ _id
				for(Field field : fields){
					if("_id".equals(field.getName()))
						return "_id";
				}
				
				for(Field field : fields){
					if("id".equals(field.getName()))
						return "id";
				}
			}
		}else{
			throw new RuntimeException("this model["+clazz+"] has no field");
		}
		return primaryKey;
	}
	
	
	/**
	 * ¸ù¾ÝÊµÌåÀà »ñµÃ ÊµÌåÀà¶ÔÓ¦µÄ±íÃû
	 * @param entity
	 * @return
	 */
	public static Field getPrimaryKeyField(Class<?> clazz) {
		Field primaryKeyField = null ;
		Field[] fields = clazz.getDeclaredFields();
		if(fields != null){
			
			for(Field field : fields){ //ï¿½ï¿½å³°ï¿½ï¿?IDå¨???¨Ð?
				if(field.getAnnotation(Id.class) != null){
					primaryKeyField = field;
					break;
				}
			}
			
			if(primaryKeyField == null){ //å¨????ï¿½ï¿½IDå¨???¨Ð?
				for(Field field : fields){
					if("_id".equals(field.getName())){
						primaryKeyField = field;
						break;
					}
				}
			}
			
			if(primaryKeyField == null){ // æ¿¡ï¿½ï¿½ï¿½ï¿½å?????ï¿½ï¿½_idï¿½ï¿½ï¿½ç??ï¿½å??ï¿?
				for(Field field : fields){
					if("id".equals(field.getName())){
						primaryKeyField = field;
						break;
					}
				}
			}
			
		}else{
			throw new RuntimeException("this model["+clazz+"] has no field");
		}
		return primaryKeyField;
	}
	
	
	/**
	 * ¸ù¾ÝÊµÌåÀà »ñµÃ ÊµÌåÀà¶ÔÓ¦µÄ±íÃû
	 * @param entity
	 * @return
	 */
	public static String getPrimaryKeyFieldName(Class<?> clazz) {
		Field f = getPrimaryKeyField(clazz);
		return f==null ? null:f.getName();
	}
	
	
	
	/**
	 * ½«¶ÔÏó×ª»»ÎªContentValues
	 * 
	 * @param entity
	 * @param selective ÊÇ·ñºöÂÔ ÖµÎªnullµÄ×Ö¶Î
	 * @return
	 */
	public static List<Property> getPropertyList(Class<?> clazz) {
		
		List<Property> plist = new ArrayList<Property>();
		try {
			Field[] fs = clazz.getDeclaredFields();
			String primaryKeyFieldName = getPrimaryKeyFieldName(clazz);
			for (Field f : fs) {
				//è¹?ï¿½æ¤¤ç»?ï¿½ï¿½ï¿½ï¿½???ï¿½ï¿½ï¿½ï¿½???ï¿½ï¿½ç»?è¯²ï¿½ï¿½ï¿½ï¿½ï¿½å¨????ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½??µï¿½ï¿½ï¿½ï¿½ï¿½???ï¿½å??ï¿?
				if(!FieldUtils.isTransient(f)){
					if (FieldUtils.isBaseDateType(f)) {
						
						if(f.getName().equals(primaryKeyFieldName)) //??©ï¿½å©????å¯?ï¿½ï¿½ï¿?
							continue;
						
						Property property = new Property();
					
						property.setColumn(FieldUtils.getColumnByField(f));
						property.setFieldName(f.getName());
						property.setDataType(f.getType());
						property.setDefaultValue(FieldUtils.getPropertyDefaultValue(f));
						property.setSet(FieldUtils.getFieldSetMethod(clazz, f));
						property.setGet(FieldUtils.getFieldGetMethod(clazz, f));
						property.setField(f);
						
						plist.add(property);
					}
				}
			}
			return plist;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	
	/**
	 * ½«¶ÔÏó×ª»»ÎªContentValues
	 * 
	 * @param entity
	 * @param selective ÊÇ·ñºöÂÔ ÖµÎªnullµÄ×Ö¶Î
	 * @return
	 */
	public static List<ManyToOne> getManyToOneList(Class<?> clazz) {
		
		List<ManyToOne> mList = new ArrayList<ManyToOne>();
		try {
			Field[] fs = clazz.getDeclaredFields();
			for (Field f : fs) {
				if (!FieldUtils.isTransient(f) && FieldUtils.isManyToOne(f)) {
					
					ManyToOne mto = new ManyToOne();
                    //æ¿¡ï¿½ï¿½ï¿½ï¿½ç»«è¯²ï¿½ï¿½æ??ï¿?ManyToOneLazyLoaderï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ç»?ï¿½æ??ï¿½æ??ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½é¢?ï¿½ï¿½æ¶?ï¿?manyClass???ï¿½æ??ï¿½ï¿½ï¿½ç?°ï¿½ï¿½æµ£ï¿½é??ï¿? 2013-7-26
                    if(f.getType()==ManyToOneLazyLoader.class){
                        Class<?> pClazz = (Class<?>)((ParameterizedType)f.getGenericType()).getActualTypeArguments()[1];
                        if(pClazz!=null)
                            mto.setManyClass(pClazz);
                    }else {
					    mto.setManyClass(f.getType());
                    }
					mto.setColumn(FieldUtils.getColumnByField(f));
					mto.setFieldName(f.getName());
					mto.setDataType(f.getType());	
					mto.setSet(FieldUtils.getFieldSetMethod(clazz, f));
					mto.setGet(FieldUtils.getFieldGetMethod(clazz, f));
					
					mList.add(mto);
				}
			}
			return mList;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	
	/**
	 * ½«¶ÔÏó×ª»»ÎªContentValues
	 * 
	 * @param entity
	 * @param selective ÊÇ·ñºöÂÔ ÖµÎªnullµÄ×Ö¶Î
	 * @return
	 */
	public static List<OneToMany> getOneToManyList(Class<?> clazz) {
		
		List<OneToMany> oList = new ArrayList<OneToMany>();
		try {
			Field[] fs = clazz.getDeclaredFields();
			for (Field f : fs) {
				if (!FieldUtils.isTransient(f) && FieldUtils.isOneToMany(f)) {
					
					OneToMany otm = new OneToMany();
					
					otm.setColumn(FieldUtils.getColumnByField(f));
					otm.setFieldName(f.getName());
					
					Type type = f.getGenericType();
					
					if(type instanceof ParameterizedType){
						ParameterizedType pType = (ParameterizedType) f.getGenericType();
                        //æ¿¡ï¿½ï¿½ï¿½ï¿½ç»«è¯²ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½é¢?è´?2ï¿½ï¿½ï¿½ç?????è´?ï¿½ï¿½ï¿?LazyLoader 2013-7-25
                        if(pType.getActualTypeArguments().length==1){
						    Class<?> pClazz = (Class<?>)pType.getActualTypeArguments()[0];
						    if(pClazz!=null)
							    otm.setOneClass(pClazz);
                        }else{
                            Class<?> pClazz = (Class<?>)pType.getActualTypeArguments()[1];
                            if(pClazz!=null)
                                otm.setOneClass(pClazz);
                        }
					}else{
						throw new DbException("getOneToManyList Exception:"+f.getName()+"'s type is null");
					}
					/*æ·?ï¿½å??ï½?è¢?ï¿½ï¿½ï¿½ç?§ï¿½ï¿½ï¿½å¥¸ï¿½ï¿½ç??ï¿½ï¿½ï¿½ï¿½bug???ï¿?f.getClass??©ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿?Filed*/
					otm.setDataType(f.getType());
					otm.setSet(FieldUtils.getFieldSetMethod(clazz, f));
					otm.setGet(FieldUtils.getFieldGetMethod(clazz, f));
					
					oList.add(otm);
				}
			}
			return oList;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}	
	
	
}
