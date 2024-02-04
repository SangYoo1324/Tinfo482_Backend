package tinfo.project.tinfo482.repo.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import tinfo.project.tinfo482.entity.inventory.Acc;

public interface AccRepository extends JpaRepository<Acc,Long> {
}
