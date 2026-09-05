package com.aep.cursoLivre.services;

import com.aep.cursoLivre.models.DatabaseSequenceModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
@RequiredArgsConstructor
public class GeradorSequencialService {

    private final MongoOperations mongoOperations;

    public long generateSequence(String seqName) {
        DatabaseSequenceModel counter = mongoOperations.findAndModify(
                Query.query(where("_id").is(seqName)),
                new Update().inc("seq", 1),
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                DatabaseSequenceModel.class);

        return counter != null ? counter.getSeq() : 1;
    }

}
