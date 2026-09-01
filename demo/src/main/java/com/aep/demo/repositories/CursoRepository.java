package com.aep.demo.repositories;

import com.aep.demo.models.CursoModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CursoRepository extends MongoRepository<CursoModel,Long> {
}
