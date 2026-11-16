-- V2: Seed data - real drugs and known interactions

-- ==================== DRUGS ====================
INSERT INTO drugs (generic_name, brand_names, drug_class, description) VALUES
('warfarin',       'Coumadin, Jantoven',               'Anticoagulant',          'Vitamin K antagonist anticoagulant used to prevent and treat blood clots.'),
('aspirin',        'Bayer, Ecotrin, Bufferin',          'NSAID / Antiplatelet',   'Non-steroidal anti-inflammatory drug also used as an antiplatelet agent.'),
('ibuprofen',      'Advil, Motrin, Nurofen',            'NSAID',                  'Non-steroidal anti-inflammatory drug used for pain, fever, and inflammation.'),
('simvastatin',    'Zocor',                             'Statin',                 'HMG-CoA reductase inhibitor used to lower cholesterol.'),
('amiodarone',     'Cordarone, Pacerone',               'Antiarrhythmic',         'Class III antiarrhythmic used for ventricular and atrial arrhythmias.'),
('metformin',      'Glucophage, Glumetza',              'Biguanide',              'First-line oral antidiabetic agent that reduces hepatic glucose production.'),
('lisinopril',     'Zestril, Prinivil',                 'ACE Inhibitor',          'Angiotensin-converting enzyme inhibitor for hypertension and heart failure.'),
('fluoxetine',     'Prozac, Sarafem',                   'SSRI',                   'Selective serotonin reuptake inhibitor antidepressant.'),
('phenelzine',     'Nardil',                            'MAOI',                   'Monoamine oxidase inhibitor antidepressant.'),
('clopidogrel',    'Plavix',                            'Antiplatelet',           'ADP receptor antagonist antiplatelet used to prevent arterial thrombosis.'),
('omeprazole',     'Prilosec, Losec',                   'Proton Pump Inhibitor',  'Proton pump inhibitor used for GERD and peptic ulcer disease.'),
('digoxin',        'Lanoxin',                           'Cardiac Glycoside',      'Cardiac glycoside used for heart failure and atrial fibrillation.'),
('potassium',      'K-Dur, Klor-Con, K-Tab',            'Electrolyte Supplement', 'Potassium supplement used to prevent or treat hypokalemia.'),
('methotrexate',   'Rheumatrex, Trexall',               'Antimetabolite',         'Disease-modifying antirheumatic drug and antimetabolite chemotherapy agent.'),
('ciprofloxacin',  'Cipro',                             'Fluoroquinolone',        'Broad-spectrum fluoroquinolone antibiotic.'),
('atorvastatin',   'Lipitor',                           'Statin',                 'HMG-CoA reductase inhibitor for hypercholesterolemia and cardiovascular risk reduction.'),
('amlodipine',     'Norvasc',                           'Calcium Channel Blocker','Dihydropyridine calcium channel blocker for hypertension and angina.');

-- ==================== INTERACTIONS ====================

-- 1. Warfarin + Aspirin: MAJOR - increased bleeding risk
INSERT INTO drug_interactions (drug1_id, drug2_id, severity_level, interaction_type, mechanism, clinical_effect, recommendation, evidence_level)
SELECT d1.id, d2.id, 'MAJOR', 'PHARMACODYNAMIC',
  'Aspirin inhibits platelet aggregation via COX-1 inhibition; combined with warfarin anticoagulation significantly increases hemorrhagic risk. Aspirin may also displace warfarin from plasma protein binding sites.',
  'Increased risk of serious bleeding including GI hemorrhage, intracranial bleeding, and prolonged bleeding time. INR may also be elevated.',
  'Avoid concomitant use unless specifically indicated (e.g., mechanical heart valve). If co-administration is necessary, use lowest effective aspirin dose and monitor INR closely. Consider PPI prophylaxis.',
  'WELL_DOCUMENTED'
FROM drugs d1, drugs d2 WHERE d1.generic_name = 'warfarin' AND d2.generic_name = 'aspirin';

-- 2. Warfarin + Ibuprofen: MAJOR - increased bleeding risk
INSERT INTO drug_interactions (drug1_id, drug2_id, severity_level, interaction_type, mechanism, clinical_effect, recommendation, evidence_level)
SELECT d1.id, d2.id, 'MAJOR', 'BOTH',
  'NSAIDs inhibit platelet function via COX-1 and can increase anticoagulant effect by displacing warfarin from albumin. NSAIDs also cause GI mucosal injury increasing GI bleed risk.',
  'Significantly increased risk of GI bleeding and other hemorrhagic complications. INR may be elevated unpredictably.',
  'Avoid combination. Use acetaminophen for analgesia/antipyresis instead. If NSAID is required, monitor INR closely and use short-term only with gastroprotection.',
  'WELL_DOCUMENTED'
FROM drugs d1, drugs d2 WHERE d1.generic_name = 'warfarin' AND d2.generic_name = 'ibuprofen';

-- 3. Simvastatin + Amiodarone: CONTRAINDICATED - rhabdomyolysis risk
INSERT INTO drug_interactions (drug1_id, drug2_id, severity_level, interaction_type, mechanism, clinical_effect, recommendation, evidence_level)
SELECT d1.id, d2.id, 'CONTRAINDICATED', 'PHARMACOKINETIC',
  'Amiodarone and its active metabolite desethylamiodarone inhibit CYP3A4 and CYP2C8, markedly increasing simvastatin plasma concentrations. Elevated statin levels increase myotoxicity risk.',
  'High risk of myopathy and rhabdomyolysis with potential acute kidney injury from myoglobinuria.',
  'Contraindicated combination. If amiodarone is required in a patient needing statin therapy, switch to pravastatin or rosuvastatin which are not primarily CYP3A4 substrates. Simvastatin dose should not exceed 20mg/day with amiodarone (FDA labeling); many guidelines recommend complete avoidance.',
  'WELL_DOCUMENTED'
FROM drugs d1, drugs d2 WHERE d1.generic_name = 'simvastatin' AND d2.generic_name = 'amiodarone';

-- 4. Metformin + Contrast dye: MODERATE - lactic acidosis risk
INSERT INTO drug_interactions (drug1_id, drug2_id, severity_level, interaction_type, mechanism, clinical_effect, recommendation, evidence_level)
SELECT d1.id, d2.id, 'MODERATE', 'PHARMACOKINETIC',
  'Iodinated contrast media can cause transient renal impairment, reducing metformin clearance. Accumulation of metformin impairs mitochondrial complex I, increasing lactate production.',
  'Risk of metformin-associated lactic acidosis (MALA), a rare but potentially fatal complication, particularly in patients with pre-existing renal impairment.',
  'Hold metformin 48 hours before and after iodinated contrast administration in patients with eGFR <60 mL/min/1.73m2. Restart only after reassessing renal function. In patients with eGFR ≥60, assess risk individually.',
  'CLINICAL_STUDY'
FROM drugs d1, drugs d2 WHERE d1.generic_name = 'metformin' AND d2.generic_name = 'ciprofloxacin';

-- 5. Lisinopril + Potassium: MODERATE - hyperkalemia
INSERT INTO drug_interactions (drug1_id, drug2_id, severity_level, interaction_type, mechanism, clinical_effect, recommendation, evidence_level)
SELECT d1.id, d2.id, 'MODERATE', 'PHARMACODYNAMIC',
  'ACE inhibitors reduce aldosterone production, decreasing renal potassium excretion. Exogenous potassium supplementation on top of this effect can lead to potassium accumulation.',
  'Hyperkalemia, which can cause cardiac arrhythmias and muscle weakness. Risk is greatest in patients with renal impairment or diabetes.',
  'Monitor serum potassium and renal function regularly. Avoid high-dose potassium supplementation. Instruct patients to avoid potassium-rich salt substitutes. Hold or reduce potassium supplementation if K+ exceeds 5.0 mEq/L.',
  'WELL_DOCUMENTED'
FROM drugs d1, drugs d2 WHERE d1.generic_name = 'lisinopril' AND d2.generic_name = 'potassium';

-- 6. SSRI (fluoxetine) + MAOI (phenelzine): CONTRAINDICATED - serotonin syndrome
INSERT INTO drug_interactions (drug1_id, drug2_id, severity_level, interaction_type, mechanism, clinical_effect, recommendation, evidence_level)
SELECT d1.id, d2.id, 'CONTRAINDICATED', 'PHARMACODYNAMIC',
  'SSRIs block serotonin reuptake while MAOIs block serotonin degradation; combined effect causes massive accumulation of synaptic serotonin, triggering serotonin syndrome.',
  'Serotonin syndrome: potentially life-threatening condition characterized by hyperthermia, neuromuscular abnormalities (clonus, hyperreflexia, tremor), and autonomic instability. Can be fatal.',
  'Absolutely contraindicated. Allow at least 14 days after stopping an MAOI before starting fluoxetine. Due to fluoxetine''s long half-life, wait at least 5 weeks after stopping fluoxetine before starting an MAOI. Use alternative antidepressants.',
  'WELL_DOCUMENTED'
FROM drugs d1, drugs d2 WHERE d1.generic_name = 'fluoxetine' AND d2.generic_name = 'phenelzine';

-- 7. Clopidogrel + Omeprazole: MODERATE - reduced efficacy
INSERT INTO drug_interactions (drug1_id, drug2_id, severity_level, interaction_type, mechanism, clinical_effect, recommendation, evidence_level)
SELECT d1.id, d2.id, 'MODERATE', 'PHARMACOKINETIC',
  'Clopidogrel is a prodrug requiring CYP2C19-mediated bioactivation. Omeprazole is a potent CYP2C19 inhibitor, reducing formation of the active thiol metabolite by approximately 45-47%.',
  'Reduced antiplatelet effect of clopidogrel, with potential for increased cardiovascular events including stent thrombosis in patients with coronary artery disease.',
  'Consider switching to pantoprazole or rabeprazole (weaker CYP2C19 inhibitors) if gastroprotection is needed. Some guidelines advise against routine co-prescription. Monitor for signs of reduced antiplatelet effect.',
  'CLINICAL_STUDY'
FROM drugs d1, drugs d2 WHERE d1.generic_name = 'clopidogrel' AND d2.generic_name = 'omeprazole';

-- 8. Digoxin + Amiodarone: MAJOR - toxicity
INSERT INTO drug_interactions (drug1_id, drug2_id, severity_level, interaction_type, mechanism, clinical_effect, recommendation, evidence_level)
SELECT d1.id, d2.id, 'MAJOR', 'PHARMACOKINETIC',
  'Amiodarone inhibits P-glycoprotein (P-gp) and reduces renal and non-renal clearance of digoxin by approximately 50%. Amiodarone also displaces digoxin from tissue binding sites.',
  'Increased serum digoxin levels leading to digoxin toxicity: nausea, vomiting, bradycardia, heart block, and life-threatening arrhythmias including ventricular fibrillation.',
  'Reduce digoxin dose by 30-50% when initiating amiodarone. Monitor digoxin levels closely and titrate to therapeutic range (0.5-0.9 ng/mL for heart failure). Watch for signs of toxicity and adjust based on renal function.',
  'WELL_DOCUMENTED'
FROM drugs d1, drugs d2 WHERE d1.generic_name = 'digoxin' AND d2.generic_name = 'amiodarone';

-- 9. Methotrexate + Ibuprofen: MAJOR - methotrexate toxicity
INSERT INTO drug_interactions (drug1_id, drug2_id, severity_level, interaction_type, mechanism, clinical_effect, recommendation, evidence_level)
SELECT d1.id, d2.id, 'MAJOR', 'PHARMACOKINETIC',
  'NSAIDs reduce renal blood flow and inhibit tubular secretion of methotrexate, decreasing its clearance. This can lead to substantial accumulation of methotrexate.',
  'Methotrexate toxicity including severe myelosuppression, mucositis, hepatotoxicity, and nephrotoxicity. Even low-dose methotrexate can accumulate to toxic levels.',
  'Avoid NSAIDs with high-dose methotrexate. With low-dose methotrexate (rheumatoid arthritis doses), use with extreme caution and only if necessary. Monitor CBC and renal function closely.',
  'CLINICAL_STUDY'
FROM drugs d1, drugs d2 WHERE d1.generic_name = 'methotrexate' AND d2.generic_name = 'ibuprofen';

-- 10. Ciprofloxacin + Warfarin: MODERATE - increased anticoagulant effect
INSERT INTO drug_interactions (drug1_id, drug2_id, severity_level, interaction_type, mechanism, clinical_effect, recommendation, evidence_level)
SELECT d1.id, d2.id, 'MODERATE', 'BOTH',
  'Ciprofloxacin inhibits CYP1A2, reducing warfarin clearance, particularly the R-enantiomer. Additionally, fluoroquinolones may suppress gut bacteria that produce vitamin K, further potentiating anticoagulation.',
  'Elevated INR and increased bleeding risk. INR elevation can occur within 2-3 days of starting ciprofloxacin.',
  'Monitor INR closely during and for 7-10 days after ciprofloxacin course. Consider prophylactic INR check at day 3-4 after starting antibiotic. Educate patient on bleeding signs.',
  'CLINICAL_STUDY'
FROM drugs d1, drugs d2 WHERE d1.generic_name = 'ciprofloxacin' AND d2.generic_name = 'warfarin';

-- 11. Simvastatin + Amlodipine: MODERATE - myopathy risk
INSERT INTO drug_interactions (drug1_id, drug2_id, severity_level, interaction_type, mechanism, clinical_effect, recommendation, evidence_level)
SELECT d1.id, d2.id, 'MODERATE', 'PHARMACOKINETIC',
  'Amlodipine moderately inhibits CYP3A4, the primary enzyme responsible for simvastatin metabolism, leading to increased simvastatin exposure (AUC approximately doubled).',
  'Increased risk of simvastatin-related myopathy and rhabdomyolysis due to elevated plasma concentrations.',
  'Limit simvastatin dose to 20mg/day when co-administered with amlodipine. Consider alternative statin (pravastatin, rosuvastatin) not metabolized by CYP3A4 if higher statin doses are required.',
  'CLINICAL_STUDY'
FROM drugs d1, drugs d2 WHERE d1.generic_name = 'simvastatin' AND d2.generic_name = 'amlodipine';

-- 12. Atorvastatin + Amiodarone: MODERATE - myopathy risk
INSERT INTO drug_interactions (drug1_id, drug2_id, severity_level, interaction_type, mechanism, clinical_effect, recommendation, evidence_level)
SELECT d1.id, d2.id, 'MODERATE', 'PHARMACOKINETIC',
  'Amiodarone inhibits CYP3A4, the primary metabolic pathway for atorvastatin, increasing atorvastatin plasma concentrations.',
  'Increased risk of myopathy and rhabdomyolysis. Risk is dose-dependent.',
  'Use lowest effective atorvastatin dose. Consider pravastatin or rosuvastatin as alternatives. Monitor for muscle symptoms (pain, weakness, tenderness) and check CK if symptomatic.',
  'CLINICAL_STUDY'
FROM drugs d1, drugs d2 WHERE d1.generic_name = 'atorvastatin' AND d2.generic_name = 'amiodarone';

-- 13. Aspirin + Ibuprofen: MINOR - reduced aspirin efficacy
INSERT INTO drug_interactions (drug1_id, drug2_id, severity_level, interaction_type, mechanism, clinical_effect, recommendation, evidence_level)
SELECT d1.id, d2.id, 'MINOR', 'PHARMACODYNAMIC',
  'Ibuprofen can competitively inhibit the COX-1 binding site on platelets, blocking aspirin''s irreversible acetylation. This interaction is time-dependent and reversible.',
  'Potential reduction in aspirin''s cardioprotective antiplatelet effect when ibuprofen is taken close in time to aspirin.',
  'If using aspirin for cardiovascular protection, take aspirin at least 30 minutes before or 8 hours after ibuprofen. Consider using acetaminophen or a selective COX-2 inhibitor as alternative analgesic.',
  'CLINICAL_STUDY'
FROM drugs d1, drugs d2 WHERE d1.generic_name = 'aspirin' AND d2.generic_name = 'ibuprofen';

-- 14. Lisinopril + Ibuprofen: MODERATE - reduced antihypertensive efficacy and renal risk
INSERT INTO drug_interactions (drug1_id, drug2_id, severity_level, interaction_type, mechanism, clinical_effect, recommendation, evidence_level)
SELECT d1.id, d2.id, 'MODERATE', 'PHARMACODYNAMIC',
  'NSAIDs inhibit prostaglandin synthesis, which is required for the vasodilatory and natriuretic effects of ACE inhibitors. NSAIDs also cause afferent arteriolar constriction, reducing GFR.',
  'Reduced antihypertensive efficacy of lisinopril, possible acute kidney injury (especially in elderly, heart failure, or volume-depleted patients). Risk of hyperkalemia also increased.',
  'Avoid concomitant use, especially in elderly patients, those with heart failure, or reduced renal function. If NSAID is necessary, use shortest duration and lowest dose, monitor blood pressure and renal function.',
  'WELL_DOCUMENTED'
FROM drugs d1, drugs d2 WHERE d1.generic_name = 'lisinopril' AND d2.generic_name = 'ibuprofen';

-- 15. Fluoxetine + Warfarin: MODERATE - increased anticoagulant effect
INSERT INTO drug_interactions (drug1_id, drug2_id, severity_level, interaction_type, mechanism, clinical_effect, recommendation, evidence_level)
SELECT d1.id, d2.id, 'MODERATE', 'PHARMACOKINETIC',
  'Fluoxetine and its active metabolite norfluoxetine inhibit CYP2C9, the primary enzyme responsible for metabolism of S-warfarin (the more potent enantiomer).',
  'Elevated INR and increased bleeding risk. The effect may be delayed due to fluoxetine''s long half-life and accumulation phase.',
  'Monitor INR closely when initiating, adjusting, or discontinuing fluoxetine in patients on warfarin. INR check within 1 week of fluoxetine initiation recommended. Watch for bleeding signs.',
  'CLINICAL_STUDY'
FROM drugs d1, drugs d2 WHERE d1.generic_name = 'fluoxetine' AND d2.generic_name = 'warfarin';

-- 16. Digoxin + Ciprofloxacin: MODERATE - increased digoxin levels
INSERT INTO drug_interactions (drug1_id, drug2_id, severity_level, interaction_type, mechanism, clinical_effect, recommendation, evidence_level)
SELECT d1.id, d2.id, 'MODERATE', 'PHARMACOKINETIC',
  'In a subset of patients (~10%), gut bacteria (Eggerthella lenta) convert digoxin to inactive metabolites. Ciprofloxacin eradicates these bacteria, increasing digoxin bioavailability. Ciprofloxacin also inhibits P-gp.',
  'Elevated serum digoxin concentrations and risk of digoxin toxicity (bradycardia, GI symptoms, visual changes, arrhythmias).',
  'Monitor digoxin levels and clinical signs of toxicity during antibiotic course. Consider dose reduction. Monitor ECG for signs of digoxin toxicity.',
  'CASE_REPORT'
FROM drugs d1, drugs d2 WHERE d1.generic_name = 'digoxin' AND d2.generic_name = 'ciprofloxacin';

-- 17. Clopidogrel + Aspirin: MINOR - minor additive bleeding risk
INSERT INTO drug_interactions (drug1_id, drug2_id, severity_level, interaction_type, mechanism, clinical_effect, recommendation, evidence_level)
SELECT d1.id, d2.id, 'MINOR', 'PHARMACODYNAMIC',
  'Both agents inhibit platelet function through different mechanisms (ADP receptor and COX-1 respectively). Combination provides additive antiplatelet effect but also additive bleeding risk.',
  'Increased risk of minor to moderate bleeding events (bruising, nosebleeds, GI bleeding). Dual antiplatelet therapy is a deliberate treatment strategy in ACS/PCI patients.',
  'This combination is clinically indicated in certain conditions (ACS, post-PCI). Use with awareness of bleeding risk. PPI co-prescription recommended. Duration of dual therapy should follow current guidelines.',
  'WELL_DOCUMENTED'
FROM drugs d1, drugs d2 WHERE d1.generic_name = 'clopidogrel' AND d2.generic_name = 'aspirin';

-- 18. Metformin + Lisinopril: MINOR - no clinically significant interaction
INSERT INTO drug_interactions (drug1_id, drug2_id, severity_level, interaction_type, mechanism, clinical_effect, recommendation, evidence_level)
SELECT d1.id, d2.id, 'MINOR', 'PHARMACODYNAMIC',
  'ACE inhibitors can improve insulin sensitivity and have hypoglycemic effects. When combined with metformin, there may be a modest additive glucose-lowering effect.',
  'Minor risk of hypoglycemia, particularly in patients with reduced caloric intake. Generally a beneficial combination in diabetic patients with hypertension or heart failure.',
  'Monitor blood glucose, particularly when initiating or dose-adjusting either agent. This combination is frequently prescribed and generally beneficial in diabetic patients with cardiovascular risk.',
  'CLINICAL_STUDY'
FROM drugs d1, drugs d2 WHERE d1.generic_name = 'metformin' AND d2.generic_name = 'lisinopril';

-- 19. Warfarin + Amiodarone: MAJOR - markedly increased anticoagulation
INSERT INTO drug_interactions (drug1_id, drug2_id, severity_level, interaction_type, mechanism, clinical_effect, recommendation, evidence_level)
SELECT d1.id, d2.id, 'MAJOR', 'PHARMACOKINETIC',
  'Amiodarone and its active metabolite potently inhibit CYP2C9 (primary warfarin S-enantiomer metabolizer) and CYP3A4. This interaction has a delayed onset due to amiodarone''s extremely long half-life (40-55 days).',
  'Markedly elevated INR and risk of serious hemorrhage. INR elevation may not peak for 4-6 weeks after initiating amiodarone due to its accumulation pharmacokinetics.',
  'Reduce warfarin dose by 30-50% when initiating amiodarone. Monitor INR at least weekly for first month, then monthly. The interaction persists for months after stopping amiodarone. Document baseline INR.',
  'WELL_DOCUMENTED'
FROM drugs d1, drugs d2 WHERE d1.generic_name = 'warfarin' AND d2.generic_name = 'amiodarone';

-- 20. Atorvastatin + Amlodipine: MINOR - mild statin exposure increase
INSERT INTO drug_interactions (drug1_id, drug2_id, severity_level, interaction_type, mechanism, clinical_effect, recommendation, evidence_level)
SELECT d1.id, d2.id, 'MINOR', 'PHARMACOKINETIC',
  'Amlodipine mildly inhibits CYP3A4, slightly increasing atorvastatin AUC by approximately 18%. This is a weaker interaction than seen with simvastatin.',
  'Modest increase in atorvastatin exposure. Clinical significance is generally low at standard doses; mild increase in myopathy risk at higher atorvastatin doses.',
  'Generally no dose adjustment needed. Avoid highest atorvastatin doses (80mg) if other myopathy risk factors present. Monitor for muscle symptoms.',
  'CLINICAL_STUDY'
FROM drugs d1, drugs d2 WHERE d1.generic_name = 'atorvastatin' AND d2.generic_name = 'amlodipine';
