package com.aep.demo.repositories;

import com.aep.demo.models.AlunoModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AlunoRepository  extends MongoRepository<AlunoModel,Long> {
}
