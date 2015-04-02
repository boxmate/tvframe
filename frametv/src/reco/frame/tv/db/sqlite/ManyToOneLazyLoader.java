package reco.frame.tv.db.sqlite;

import reco.frame.tv.TvDb;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * ä¸?å¯¹å??å»¶è?????è½½ç±»
 * Created by pwy on 13-7-25.
 * @param <O> å®¿ä¸»å®?ä½????class
 * @param <M> å¤???¾å??ä½?class
 */
public class ManyToOneLazyLoader<M,O> {
    M manyEntity;
    Class<M> manyClazz;
    Class<O> oneClazz;
    TvDb db;
    /**
     * ??¨ä??
     */
    private Object fieldValue;
    public ManyToOneLazyLoader(M manyEntity, Class<M> manyClazz, Class<O> oneClazz, TvDb db){
        this.manyEntity = manyEntity;
        this.manyClazz = manyClazz;
        this.oneClazz = oneClazz;
        this.db = db;
    }
    O oneEntity;
    boolean hasLoaded = false;

    /**
     * å¦??????°æ????????è½½ï?????è°????loadManyToOneå¡??????°æ??
     * @return
     */
    public O get(){
        if(oneEntity==null && !hasLoaded){
            this.db.loadManyToOne(null,this.manyEntity,this.manyClazz,this.oneClazz);
            hasLoaded = true;
        }
        return oneEntity;
    }
    public void set(O value){
        oneEntity = value;
    }

    public Object getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(Object fieldValue) {
        this.fieldValue = fieldValue;
    }
}
