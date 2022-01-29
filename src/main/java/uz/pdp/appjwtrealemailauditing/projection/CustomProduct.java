package uz.pdp.appjwtrealemailauditing.projection;

import org.springframework.data.rest.core.config.Projection;
import uz.pdp.appjwtrealemailauditing.entity.Product;

import java.sql.Timestamp;
import java.util.UUID;

@Projection(types = Product.class)
public interface CustomProduct {

    UUID getId();

    String getName();

    UUID getCreatedBy();

    UUID getUpdatedBy();

    Timestamp getCreatedAt();

    Timestamp getUpdatedAt();
}
