
package com.aep.cursoLivre.repositories;

import com.aep.cursoLivre.models.AlunoModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AlunoRepository
        extends MongoRepository<AlunoModel, String> {

    Optional<AlunoModel> findByAlunoId(Long alunoId);

}
