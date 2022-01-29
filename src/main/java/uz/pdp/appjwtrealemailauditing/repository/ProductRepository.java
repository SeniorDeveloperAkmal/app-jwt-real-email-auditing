package uz.pdp.appjwtrealemailauditing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uz.pdp.appjwtrealemailauditing.entity.Product;
import uz.pdp.appjwtrealemailauditing.projection.CustomProduct;

import java.util.UUID;
@RepositoryRestResource(path = "product", excerptProjection = CustomProduct.class)
public interface ProductRepository extends JpaRepository<Product, UUID> {
}
