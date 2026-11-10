
package com.aep.cursoLivre.repositories;

import com.aep.cursoLivre.models.ProfessorModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProfessorRepository
        extends MongoRepository<ProfessorModel, String> {

    Optional<ProfessorModel> findByProfessorId(Long professorId);

    Optional<ProfessorModel> findByEmail(String email);
}
