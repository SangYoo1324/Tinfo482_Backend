package tinfo.project.tinfo482.repo.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import tinfo.project.tinfo482.entity.transaction.Cart;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findCartByMember_Id(Long id);
}
