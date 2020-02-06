package com.baisaiju.springboot.entities;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @author dav1d
 */
@Document(collection = "user")
public class User {
    private ObjectId id;
    private String openId;
    private List<String> type;
    private List<ObjectId> favorite;

    public List<String> getType(){
        return type;
    }

    public void setType(List<String> type){
        this.type = type;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public List<ObjectId> getFavorite() {
        return favorite;
    }

    public void setFavorite(List<ObjectId> favorite) {
        this.favorite = favorite;
    }


    public void addFavorite(ObjectId objectId){
        this.getFavorite().add(objectId);
    }

    public void delFavorite(ObjectId objectId){
        List<ObjectId> tmpList = this.getFavorite();
        for (int index = 0; index < this.getFavorite().size(); index++){
            if (tmpList.get(index) == objectId){
               this.getFavorite().remove(index);
               return;
            }
        }
    }

}
