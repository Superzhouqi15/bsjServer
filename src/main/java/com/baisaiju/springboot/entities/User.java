package com.baisaiju.springboot.entities;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import java.util.List;

/**
 * @author dav1d
 */
@Data
@Document(collection = "user")
public class User {
    private ObjectId id;
    private String openId;
    private List<String> type;
    private List<ObjectId> favorite;

    public void addFavorite(ObjectId objectId) {
        this.getFavorite().add(objectId);
    }

    public void delFavorite(ObjectId objectId) {
        List<ObjectId> tmpList = this.getFavorite();
        for (int index = 0; index < this.getFavorite().size(); index++) {
            if (tmpList.get(index) == objectId) {
                this.getFavorite().remove(index);
                return;
            }
        }
    }

}
