package tinfo.project.tinfo482.repo.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import tinfo.project.tinfo482.entity.transaction.CartBundle;

public interface CartBundleRepository extends JpaRepository<CartBundle,Long> {
}
