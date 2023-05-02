package ru.practicum.shareit.request.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequestorId(long userId, Sort sort);

    @Query(" select r from ItemRequest r " +
            "where r.requestor.id <> ?1")
    List<ItemRequest> findAllByOtherRequestors(long userId, Pageable pageable);

    @Query(" select r from ItemRequest r " +
            "where r.requestor.id <> ?1")
    List<ItemRequest> findAllByOtherRequestors(long userId, Sort sort);

}