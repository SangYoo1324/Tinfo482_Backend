package tinfo.project.tinfo482.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import tinfo.project.tinfo482.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
