package digit.Validator;

import digit.repository.DeathApplicationRepository;
import digit.web.models.DeathApplicationSearchCriteria;
import digit.web.models.DeathRegistrationApplication;
import digit.web.models.DeathRegistrationRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Component
public class DeathApplicationValidator {

    @Autowired
    private DeathApplicationRepository repository;

    public void validateDeathApplication(DeathRegistrationRequest deathRegistrationRequest) {
        deathRegistrationRequest.getDeathRegistrationApplications().forEach(application -> {
            if(ObjectUtils.isEmpty(application.getTenantId()))
                throw new CustomException("EG_BT_APP_ERR", "tenantId is mandatory for creating Death registration applications");
        });
    }

    public DeathRegistrationApplication validateApplicationExistence(DeathRegistrationApplication deathRegistrationApplication) {
        DeathApplicationSearchCriteria searchCriteria=DeathApplicationSearchCriteria.builder().applicationNumber(deathRegistrationApplication.getApplicationNumber()).build();
        List<DeathRegistrationApplication> apps=repository.getApplications(searchCriteria);
        System.out.println(apps.get(0));
        System.out.println("................................");
        return apps.get(0);
    }
}