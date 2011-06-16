INSERT INTO CONFIG.APPL_CONFIG (APPL_CONFIG_CODE, APPL_CONFIG_CTGY_CODE, APPL_CONFIG_VAL, APPL_CONFIG_TXT, EDITABLE_FLAG) values ('DiagnosisCodeScheme', 'CODING', 'ICD9', 'Specifies the diagnosis coding scheme. Available options are: ICD9 and ICD10. Default is ICD9' , 0);
UPDATE CONFIG.APPL_CONFIG SET APPL_CONFIG_TXT = 'Specifies the procedure coding scheme. Available options are: CPT4, HCPCS, ICD9 and ICD10. Default is ICD9' where APPL_CONFIG_CODE = 'PRIMCODESCHEME';
UPDATE CONFIG.APPL_CONFIG SET APPL_CONFIG_TXT = 'Specifies the secondary coding scheme for use with diagnosis and procedure encoding with Autocoder.  Allowed values are NONE, ICD9, ICD10, HCPCS, and CPT4.  NONE indicates dual coding is turned off.' where APPL_CONFIG_CODE = 'SECCODESCHEME';
INSERT INTO CONFIG.APPL_CONFIG (APPL_CONFIG_CODE, APPL_CONFIG_CTGY_CODE, APPL_CONFIG_VAL, APPL_CONFIG_TXT, EDITABLE_FLAG) values ('TermedMemberWarningMsgDefaultPendStatusReason', 'TXREQUEST', 'MEDRSR10', 'Indicates the code which will be used for the Outcome Status Reason when the Termed Member configurations are set to Warning.' , 1);

UPDATE CONFIG.APPL_CONFIG SET APPL_CONFIG_CODE = 'RAVCaseFindings', APPL_CONFIG_TXT = 'RAV Case Findings Section (0 - off, 1 - on).' WHERE APPL_CONFIG_CODE = 'RAVCaseFindingsSuperset';
UPDATE CONFIG.APPL_CONFIG SET APPL_CONFIG_CODE = 'RAVCostsSummary', APPL_CONFIG_VAL = '1', APPL_CONFIG_TXT = 'RAV Costs Summary Section (0 - off, 1 - on).' WHERE APPL_CONFIG_CODE = 'RAVCostsSummarySuperset';
UPDATE CONFIG.APPL_CONFIG SET APPL_CONFIG_CODE = 'RAVEmergencyRoomVisits', APPL_CONFIG_TXT = 'RAV Emergency Room Visits Section (0 - off, 1 - on).' WHERE APPL_CONFIG_CODE = 'RAVEmergencyRoomVisitsSuperset';
UPDATE CONFIG.APPL_CONFIG SET APPL_CONFIG_CODE = 'RAVHealthStatusMeasure', APPL_CONFIG_TXT = 'RAV Health Status Measure Section (0 - off, 1 - on).' WHERE APPL_CONFIG_CODE = 'RAVHealthStatusMeasureSuperset';
UPDATE CONFIG.APPL_CONFIG SET APPL_CONFIG_CODE = 'RAVInpatientFacilityAdmissions', APPL_CONFIG_VAL = '1', APPL_CONFIG_TXT = 'RAV Inpatient Facility Admissions Section (0 - off, 1 - on).' WHERE APPL_CONFIG_CODE = 'RAVInpatientFacilityAdmissionsSuperset';
UPDATE CONFIG.APPL_CONFIG SET APPL_CONFIG_CODE = 'RAVMedicalConditions', APPL_CONFIG_VAL = '1', APPL_CONFIG_TXT = 'RAV Medical Conditions Section (0 - off, 1 - on).' WHERE APPL_CONFIG_CODE = 'RAVMedicalConditionsSuperset';
UPDATE CONFIG.APPL_CONFIG SET APPL_CONFIG_CODE = 'RAVMedications', APPL_CONFIG_VAL = '1', APPL_CONFIG_TXT = 'RAV Medications Section (0 - off, 1 - on).' WHERE APPL_CONFIG_CODE = 'RAVMedicationsSuperset';
UPDATE CONFIG.APPL_CONFIG SET APPL_CONFIG_CODE = 'RAVMonitoredServices', APPL_CONFIG_VAL = '1', APPL_CONFIG_TXT = 'RAV Monitored Services Section (0 - off, 1 - on).' WHERE APPL_CONFIG_CODE = 'RAVMonitoredServicesSuperset';
UPDATE CONFIG.APPL_CONFIG SET APPL_CONFIG_CODE = 'RAVNumberofPharmacies', APPL_CONFIG_TXT = 'RAV Number of Pharmacies Section (0 - off, 1 - on).' WHERE APPL_CONFIG_CODE = 'RAVNumberofPharmaciesSuperset';
UPDATE CONFIG.APPL_CONFIG SET APPL_CONFIG_CODE = 'RAVPreventativeHealthWellness', APPL_CONFIG_VAL = '1', APPL_CONFIG_TXT = 'RAV Preventative Health Wellness Section (0 - off, 1 - on).' WHERE APPL_CONFIG_CODE = 'RAVPreventativeHealthWellnessSuperset';
UPDATE CONFIG.APPL_CONFIG SET APPL_CONFIG_CODE = 'RAVProgramSeverity', APPL_CONFIG_VAL = '1', APPL_CONFIG_TXT = 'RAV Program and Severity Section (0 - off, 1 - on).' WHERE APPL_CONFIG_CODE = 'RAVProgramSeveritySuperset';
UPDATE CONFIG.APPL_CONFIG SET APPL_CONFIG_CODE = 'RAVProvidersSeen', APPL_CONFIG_VAL = '1', APPL_CONFIG_TXT = 'RAV Providers Seen Section (0 - off, 1 - on).' WHERE APPL_CONFIG_CODE = 'RAVProvidersSeenSuperset';
UPDATE CONFIG.APPL_CONFIG SET APPL_CONFIG_CODE = 'RAVTreatmentOpportunities', APPL_CONFIG_TXT = 'RAV Treatment Opportunities Section (0 - off, 1 - on).' WHERE APPL_CONFIG_CODE = 'RAVTreatmentOpportunitiesSuperset';

INSERT INTO CONFIG.APPL_CONFIG (APPL_CONFIG_CODE,APPL_CONFIG_CTGY_CODE,APPL_CONFIG_VAL,APPL_CONFIG_TXT,EDITABLE_FLAG) VALUES ('RAVLabResults','RAV','1','RAV Lab Results Section (0 - off, 1 - on).',1);

DELETE FROM CONFIG.APPL_CONFIG WHERE APPL_CONFIG_CODE = 'RAVCareManagementSummarySuperset';
INSERT INTO CONFIG.APPL_CONFIG (APPL_CONFIG_CODE,APPL_CONFIG_CTGY_CODE,APPL_CONFIG_VAL,APPL_CONFIG_TXT,EDITABLE_FLAG) VALUES ('RAVOpenCareManagementSummary','RAV','1','RAV Open Care Management Summary Section (0 - off, 1 - on).',1);
INSERT INTO CONFIG.APPL_CONFIG (APPL_CONFIG_CODE,APPL_CONFIG_CTGY_CODE,APPL_CONFIG_VAL,APPL_CONFIG_TXT,EDITABLE_FLAG) VALUES ('RAVClosedCareManagementSummary','RAV','1','RAV Closed Care Management Summary Section (0 - off, 1 - on).',1);

DELETE FROM CONFIG.APPL_CONFIG WHERE APPL_CONFIG_CODE = 'RAVPatientSummarySuperset';
INSERT INTO CONFIG.APPL_CONFIG (APPL_CONFIG_CODE,APPL_CONFIG_CTGY_CODE,APPL_CONFIG_VAL,APPL_CONFIG_TXT,EDITABLE_FLAG) VALUES ('RAVPatientAddress','RAV','1','RAV Patient address field (0 - off, 1 - on).',1);
INSERT INTO CONFIG.APPL_CONFIG (APPL_CONFIG_CODE,APPL_CONFIG_CTGY_CODE,APPL_CONFIG_VAL,APPL_CONFIG_TXT,EDITABLE_FLAG) VALUES ('RAVPatientHomePhone','RAV','1','RAV Patient home phone field (0 - off, 1 - on).',1);
INSERT INTO CONFIG.APPL_CONFIG (APPL_CONFIG_CODE,APPL_CONFIG_CTGY_CODE,APPL_CONFIG_VAL,APPL_CONFIG_TXT,EDITABLE_FLAG) VALUES ('RAVPatientWorkPhone','RAV','1','RAV Patient work phone field (0 - off, 1 - on).',1);
INSERT INTO CONFIG.APPL_CONFIG (APPL_CONFIG_CODE,APPL_CONFIG_CTGY_CODE,APPL_CONFIG_VAL,APPL_CONFIG_TXT,EDITABLE_FLAG) VALUES ('RAVPcpMcoId','RAV','1','RAV PCP MCO ID field (0 - off, 1 - on).',1);
INSERT INTO CONFIG.APPL_CONFIG (APPL_CONFIG_CODE,APPL_CONFIG_CTGY_CODE,APPL_CONFIG_VAL,APPL_CONFIG_TXT,EDITABLE_FLAG) VALUES ('RAVPcpName','RAV','1','RAV PCP name field (0 - off, 1 - on).',1);
INSERT INTO CONFIG.APPL_CONFIG (APPL_CONFIG_CODE,APPL_CONFIG_CTGY_CODE,APPL_CONFIG_VAL,APPL_CONFIG_TXT,EDITABLE_FLAG) VALUES ('RAVPcpPhone','RAV','1','RAV PCP phone field (0 - off, 1 - on).',1);

INSERT INTO CONFIG.APPL_CONFIG (APPL_CONFIG_CODE,APPL_CONFIG_CTGY_CODE,APPL_CONFIG_VAL,APPL_CONFIG_TXT,EDITABLE_FLAG) VALUES ('RAVMonitoredServicesLabel','RAV','Monitored Services','The label header that is used for the Monitored Services section.',1);
INSERT INTO CONFIG.APPL_CONFIG (APPL_CONFIG_CODE,APPL_CONFIG_CTGY_CODE,APPL_CONFIG_VAL,APPL_CONFIG_TXT,EDITABLE_FLAG) VALUES ('RAVCaseFindingsLabel','RAV','Case Findings','The label header that is used for the Case Findings section.',1);
INSERT INTO CONFIG.APPL_CONFIG (APPL_CONFIG_CODE,APPL_CONFIG_CTGY_CODE,APPL_CONFIG_VAL,APPL_CONFIG_TXT,EDITABLE_FLAG) VALUES ('RAVProvidersSeenLabel','RAV','Providers Seen','The label header that is used for the Providers Seen section.',1);

INSERT INTO CONFIG.APPL_CONFIG (APPL_CONFIG_CODE,APPL_CONFIG_CTGY_CODE,APPL_CONFIG_VAL,APPL_CONFIG_TXT,EDITABLE_FLAG) VALUES ('RAVPocDataOffset','RAV','18','Determines the number of months of Active Care Management Summary to retrieve for display on the RAV.',1);
INSERT INTO CONFIG.APPL_CONFIG (APPL_CONFIG_CODE,APPL_CONFIG_CTGY_CODE,APPL_CONFIG_VAL,APPL_CONFIG_TXT,EDITABLE_FLAG) VALUES ('RAVStartDateOffset','RAV','0','Determines the enrollment start date offset value for determining if the member is eligible for RAV generation and display. (Elig start = member effective start date - <N> days as specified in operational parameter).',1);
INSERT INTO CONFIG.APPL_CONFIG (APPL_CONFIG_CODE,APPL_CONFIG_CTGY_CODE,APPL_CONFIG_VAL,APPL_CONFIG_TXT,EDITABLE_FLAG) VALUES ('RAVEndDateOffset','RAV','0','Determines the enrollment end date offset value for determining if the member is eligible for PCS generation and display. (Elig end = member effective end date + <N> days as specified in operational parameter).',1);

