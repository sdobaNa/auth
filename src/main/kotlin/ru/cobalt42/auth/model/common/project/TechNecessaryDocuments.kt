package ru.cobalt42.auth.model.common.project

import ru.cobalt42.auth.model.common.project.DocumentParameters

data class TechNecessaryDocuments(
    var tubeTitle: DocumentParameters = DocumentParameters(),
    var tubeLineRegistry: DocumentParameters = DocumentParameters(),
    var projectChangelog: DocumentParameters = DocumentParameters(),
    var electrodeApproval: DocumentParameters = DocumentParameters(),
    var stretchingCompensatorAct: DocumentParameters = DocumentParameters(),
    var incomingControl: DocumentParameters = DocumentParameters(),
    //FIXME position of samplingAct
    var samplingAct: DocumentParameters = DocumentParameters(),
    var complianceLabel: DocumentParameters = DocumentParameters(),
    var incomingControlJournal: DocumentParameters = DocumentParameters(),
    var hiddenWorkAct: DocumentParameters = DocumentParameters(),
    var jointConclusionVik: DocumentParameters = DocumentParameters(),
    var jointConclusionRk: DocumentParameters = DocumentParameters(),
    var jointConclusionPvkMpd: DocumentParameters = DocumentParameters(),
    var tubeLineIsolationPermit: DocumentParameters = DocumentParameters(),
    var protectiveCoatingAct: DocumentParameters = DocumentParameters(),
    var tubeLineTestPermit: DocumentParameters = DocumentParameters(),
    var stoTestAct: DocumentParameters = DocumentParameters(),
    var tubeLineDryingPermit: DocumentParameters = DocumentParameters(),
    var tubeLineDryingAct: DocumentParameters = DocumentParameters(),
    var nitrogenFillingAct: DocumentParameters = DocumentParameters(),
    var basicTestAct: DocumentParameters = DocumentParameters(),
    var installedEquipmentAct: DocumentParameters = DocumentParameters(),
    var installedArmatureAndEquipmentStatement: DocumentParameters = DocumentParameters(),
    var responsibleStructureAct: DocumentParameters = DocumentParameters(),
    var intermediateResponsibleStructureAct: DocumentParameters = DocumentParameters(),
    var passingEquipmentToInstallationAct: DocumentParameters = DocumentParameters(),
    var equipmentDefectsAct: DocumentParameters = DocumentParameters(),
    var equipmentInstallationOnFoundationAct: DocumentParameters = DocumentParameters(),
    var vesselApparatusTestAct: DocumentParameters = DocumentParameters(),
    var mechanismTestAct: DocumentParameters = DocumentParameters(),
    var flushingAct: DocumentParameters = DocumentParameters(),
    var blowdownAct: DocumentParameters = DocumentParameters(),
    var tubeCavityCleaningAct: DocumentParameters = DocumentParameters(),
    var equipmentAfterIndividualTestAct: DocumentParameters = DocumentParameters(),
    var equipmentAfterComplexTestAct: DocumentParameters = DocumentParameters(),
    var installationCertificate: DocumentParameters = DocumentParameters(),
    var tubeLineSpecification: DocumentParameters = DocumentParameters(),
    var weldingJournal: DocumentParameters = DocumentParameters(),
    var jointCorrosionProtectionJournal: DocumentParameters = DocumentParameters(),
    var corrosionWorksJournal: DocumentParameters = DocumentParameters(),
    var connectionAssemblyJournal: DocumentParameters = DocumentParameters(),
    var weldingJournalMembersOne: DocumentParameters = DocumentParameters(),
    var weldingJournalMembersTwo: DocumentParameters = DocumentParameters(),
    var weldingJournalMembersThree: DocumentParameters = DocumentParameters(),
    var detachableConnectionFitterList: DocumentParameters = DocumentParameters(),
)
