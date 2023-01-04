package com.example.application.data.service;

import com.example.application.data.entity.Booking;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

    private final BookingRepository repository;

    public BookingService(BookingRepository repository) {
        this.repository = repository;
    }

    public Optional<Booking> get(Long id) {
        return repository.findById(id);
    }

    public Booking update(Booking entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Booking> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Booking> list(Pageable pageable, Specification<Booking> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
