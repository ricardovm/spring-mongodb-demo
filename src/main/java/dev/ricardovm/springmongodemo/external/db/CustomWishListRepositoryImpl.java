package dev.ricardovm.springmongodemo.external.db;

import dev.ricardovm.springmongodemo.domain.wishlist.WishList;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
public class CustomWishListRepositoryImpl implements CustomWishListRepository {

    private final MongoTemplate mongoTemplate;

    public CustomWishListRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public boolean removeItemFromList(String clientId, String productId) {
        var query = new Query().addCriteria(where("_id").is(clientId));

        var update = new Update();
        update.pull("items", productId);

        var result = mongoTemplate.update(WishList.class).matching(query).apply(update).first();

        return result.getMatchedCount() > 0 &&
                result.getModifiedCount() > 0;
    }
}
