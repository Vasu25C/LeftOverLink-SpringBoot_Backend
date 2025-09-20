package com.leftoverlink.repository;

import com.leftoverlink.model.AcceptedDonation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AcceptedDonationRepository extends JpaRepository<AcceptedDonation, Long> {

	 @Query("SELECT COALESCE(SUM(ad.quantityAccepted), 0) FROM AcceptedDonation ad")
	    long countTotalMealsShared();
}
