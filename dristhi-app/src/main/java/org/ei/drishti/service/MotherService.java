package org.ei.drishti.service;

import org.apache.commons.lang3.StringUtils;
import org.ei.drishti.domain.form.FormSubmission;
import org.ei.drishti.dto.Action;
import org.ei.drishti.repository.AllTimelineEvents;
import org.ei.drishti.repository.MotherRepository;
import org.ei.drishti.util.IntegerUtil;

import static org.ei.drishti.domain.TimelineEvent.*;

public class MotherService {
    public static final String MOTHER_ID = "motherId";
    public static final String REFERENCE_DATE = "referenceDate";
    public static final String THAYI_CARD_NUMBER = "thayiCardNumber";
    private MotherRepository motherRepository;
    private AllTimelineEvents allTimelines;

    public MotherService(MotherRepository motherRepository, AllTimelineEvents allTimelineEvents) {
        this.motherRepository = motherRepository;
        this.allTimelines = allTimelineEvents;
    }

    public void registerANC(FormSubmission submission) {
        addTimelineEventsForMotherRegistration(submission);
    }

    public void update(Action action) {
        motherRepository.updateDetails(action.caseID(), action.details());
    }

    public void ancCareProvided(Action action) {
        allTimelines.add(forANCCareProvided(action.caseID(), action.get("visitNumber"), action.get("visitDate"), action.details()));

        String numberOfIFATabletsProvided = action.get("numberOfIFATabletsProvided");
        if (numberOfIFATabletsProvided != null && Integer.parseInt(numberOfIFATabletsProvided) > 0) {
            allTimelines.add(forIFATabletsProvided(action.caseID(), numberOfIFATabletsProvided, action.get("visitDate")));
        }
        if (StringUtils.isNotBlank(action.get("ttDose"))) {
            allTimelines.add(forTTShotProvided(action.caseID(), action.get("ttDose"), action.get("visitDate")));
        }
    }

    public void registerOutOfAreaANC(FormSubmission submission) {
        addTimelineEventsForMotherRegistration(submission);
    }

    public void updateANCOutcome(Action action) {
        motherRepository.switchToPNC(action.caseID());
        motherRepository.updateDetails(action.caseID(), action.details());
    }

    public void pncVisitHappened(Action action) {
        allTimelines.add(forMotherPNCVisit(action.caseID(), action.get("visitNumber"), action.get("visitDate"), action.details()));
        motherRepository.updateDetails(action.caseID(), action.details());

        String numberOfIFATabletsProvided = action.get("numberOfIFATabletsProvided");
        if (numberOfIFATabletsProvided != null && IntegerUtil.tryParse(numberOfIFATabletsProvided, 0) > 0) {
            allTimelines.add(forIFATabletsProvided(action.caseID(), numberOfIFATabletsProvided, action.get("visitDate")));
        }
    }

    public void close(Action action) {
        motherRepository.close(action.caseID());
    }

    private void addTimelineEventsForMotherRegistration(FormSubmission submission) {
        allTimelines.add(forStartOfPregnancy(submission.getFieldValue(MOTHER_ID), submission.getFieldValue(REFERENCE_DATE)));
        allTimelines.add(forStartOfPregnancyForEC(submission.entityId(), submission.getFieldValue(THAYI_CARD_NUMBER), submission.getFieldValue(REFERENCE_DATE)));
    }
}
