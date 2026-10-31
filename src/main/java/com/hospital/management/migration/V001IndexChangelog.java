package com.hospital.management.migration;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

@ChangeUnit(id = "v001-create-indexes", order = "001", author = "hospital-starter")
public class V001IndexChangelog {

    @Execution
    public void execution(MongoTemplate mongoTemplate) {
        mongoTemplate.indexOps("users").ensureIndex(
                new Index().on("email", Sort.Direction.ASC).unique().named("uk_users_email")
        );

        mongoTemplate.indexOps("patients").ensureIndex(
                new Index().on("medicalRecordNumber", Sort.Direction.ASC).unique().named("uk_patients_mrn")
        );

        mongoTemplate.indexOps("patients").ensureIndex(
                new Index().on("fullName", Sort.Direction.ASC).named("idx_patients_full_name")
        );
    }

    @RollbackExecution
    public void rollback(MongoTemplate mongoTemplate) {
        mongoTemplate.indexOps("users").dropIndex("uk_users_email");
        mongoTemplate.indexOps("patients").dropIndex("uk_patients_mrn");
        mongoTemplate.indexOps("patients").dropIndex("idx_patients_full_name");
    }
}
