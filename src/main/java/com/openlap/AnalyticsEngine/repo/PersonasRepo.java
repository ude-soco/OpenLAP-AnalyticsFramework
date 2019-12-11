package com.openlap.AnalyticsEngine.repo;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.openlap.AnalyticsEngine.model.Personas;


public interface PersonasRepo extends MongoRepository<Personas, String> {

	@Query(value = "{'organisation':?0 }", fields = "{'_id':1,'name':1}")
	List<Personas> findPersonNamesByOrganization(ObjectId id);

}
