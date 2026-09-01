package com.aep.cursoLivre.repositories;

import com.aep.cursoLivre.models.CursoModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CursoRepository extends MongoRepository<CursoModel,String> {
}
