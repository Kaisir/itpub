package com.wisedu.emap.itpub.cache;

import java.util.List;

/**
 * 
 * 类名称：RedisObjList    
 * 类描述：查询一组缓存结果对象
 * @version 1.0
 */
public class RedisObjList<T>
{
    /** 取到的对象集合  **/
    private List<T> objectList;
    
    /** 缓存中不存在的key  **/
    private List<String> noneList;

    public List<T> getObjectList()
    {
        return objectList;
    }

    public void setObjectList(List<T> objectList)
    {
        this.objectList = objectList;
    }

    public List<String> getNoneList()
    {
        return noneList;
    }

    public void setNoneList(List<String> noneList)
    {
        this.noneList = noneList;
    }
}
