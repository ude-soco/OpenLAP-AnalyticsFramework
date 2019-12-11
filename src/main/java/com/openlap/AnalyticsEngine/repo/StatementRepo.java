package com.openlap.AnalyticsEngine.repo;

import java.util.List;

import com.openlap.AnalyticsEngine.dto.Platforms;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.mongodb.DBObject;
import com.openlap.AnalyticsEngine.dto.Verb;
import com.openlap.AnalyticsEngine.model.Statement;
import org.springframework.security.core.parameters.P;


public interface StatementRepo extends MongoRepository<Statement, String> {


	@Query(value = "{'statement.verb':{ $exists: true  } }", fields = "{ 'id':0,'statement.verb.display':1,'statement.verb.id':1}")
	List<Verb> findAllVerbsByOrganizationAndLrs(ObjectId organizationIdObject, ObjectId lrsIdObject);

	@Query(value = "{'statement.context' : {$exists: true}}", fields = "{'statement.context.platform':1}")
	List<Platforms> findallplatformsByOrganizationAndLrs(ObjectId organizationIdObject, ObjectId lrsIdObject);

	/*
	 * @Query(value = "{'$or':[{'$and':?0},{'$and':?1}]}", fields = "?2")
	 * List<Statement> findDataByCustomQuery(DBObject queryOptionalObject, DBObject
	 * queryComplusoryObject, DBObject parametersToReceiveObject);
	 */
	@Query(value = "{'$and':[{'$and':?0},{'$and':?1},{'$and':[{'organisation':?3},{'lrs_id':?4}]}]}", fields = "?2")
	List<Statement> findDataByCustomQuery(DBObject queryObject, DBObject statementDurationObject,
			DBObject parametersToReceiveObject, ObjectId organizationIdObject, ObjectId lrsIdObject);
}
