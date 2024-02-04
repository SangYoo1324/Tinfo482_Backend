package tinfo.project.tinfo482.repo.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import tinfo.project.tinfo482.entity.inventory.Flower;

import java.util.Optional;

public interface FlowerRepository extends JpaRepository<Flower, Long> {

}
