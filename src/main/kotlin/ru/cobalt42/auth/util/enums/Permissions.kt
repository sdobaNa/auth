package ru.cobalt42.auth.util.enums

import ru.cobalt42.auth.model.auth.role.Permission

enum class Permissions(var permissions: List<Permission>) {
    PERMISSIONS(
        listOf(
            Permission(
                0,
                "person",
                "Физ. лицо",
            ),
            Permission(
                0,
                "incomingControl",
                "Документ входного контроля",
            ),
            Permission(
                0,
                "qualityDocument",
                "Документ о качестве",
            ),
            Permission(
                0,
                "tubeDocumentPack",
                "Универсальный акт Permission(трубопровод)",
            ),
            Permission(
                0,
                "project",
                "Проект",
            ),
            Permission(
                0,
                "drawing",
                "Чертеж",
            ),
            Permission(
                0,
                "drawingRevision",
                "Ревизия чертежа",
            ),
            Permission(
                0,
                "projectPart",
                "Часть проекта",
            ),
            Permission(
                0,
                "construction",
                "Объект строительства",
            ),
            Permission(
                0,
                "plot",
                "Участок строительства Permission(в разработке)",
            ),
            Permission(
                0,
                "tubeLinePart",
                "Часть линии трубопровода",
            ),
            Permission(
                0,
                "tubeLine",
                "Линия трубопровода",
            ),
            Permission(
                0,
                "jointTubeLinePart",
                "Сварной стык трубопровода",
            ),
            Permission(
                0,
                "welderAdmissionSheet",
                "Допускной лист",
            ),
            Permission(
                0,
                "naks",
                "Удостоверение НАКС",
            ),
            Permission(
                0,
                "welderSkill",
                "Умение/допуск сварщика на конкретном объекте",
            ),
            Permission(
                0,
                "user",
                "Пользователь",
            ),
            Permission(
                0,
                "role",
                "Роль Permission(права доступа)",
            ),
            Permission(
                0,
                "file",
                "Файлы",
            ),
            Permission(
                0,
                "debitCommodities",
                "Списание ТМЦ",
            ),
            Permission(
                0,
                "executiveScheme",
                "Исполнительная схема",
            ),
            Permission(
                0,
                "organization",
                "Организация",
            ),
            Permission(
                0,
                "specification",
                "Спецификация",
            ),
            Permission(
                0,
                "specificationRevision",
                "Ревизия/изменения спецификации",
            ),
            Permission(
                0,
                "work",
                "Строительно-монтажные работы",
            ),
            Permission(
                0,
                "electrodeApproval",
                "Акт проверки электродов",
            ),
            Permission(
                0,
                "equipmentAfterComplexTestAct",
                "Акт приемки оборудования после комплексного опробования",
            ),
            Permission(
                0,
                "equipmentAfterIndividualTestAct",
                "Акт приемки оборудования после индивидуального испытания",
            ),
            Permission(
                0,
                "equipmentDefectsAct",
                "Акт о выявленных дефектах оборудования",
            ),
            Permission(
                0,
                "equipmentInstallationOnFoundationAct",
                "Акт проверки установки оборудования на фундамент",
            ),
            Permission(
                0,
                "hiddenWorkAct",
                "Акт освидетельствования скрытых работ",
            ),
            Permission(
                0,
                "mechanismTestAct",
                "Акт испытания машин и механизмов",
            ),
            Permission(
                0,
                "passingEquipmentToInstallationAct",
                "Акт о приемке-передаче оборудования в монтаж",
            ),
            Permission(
                0,
                "protectiveCoatingAct",
                "Акт приемки защитного покрытия",
            ),
            Permission(
                0,
                "responsibleStructureAct",
                "Акт освидетельствования ответственных конструкций",
            ),
            Permission(
                0,
                "stretchingCompensatorAct",
                "Акт на предварительную растяжку Permission(сжатие) компенсаторов",
            ),
            Permission(
                0,
                "vesselApparatusTestAct",
                "Акт испытания сосудов и аппаратов",
            ),
            Permission(
                0,
                "technologicalCard",
                "Технологическая карта",
            ),
            Permission(
                0,
                "certificate",
                "Иное удостоверение",
            ),
            Permission(
                0,
                "tubeLineTestPermit",
                "Разрешение на испытание трубопровода",
            ),
            Permission(
                0,
                "instrument",
                "Измерительный инструмент",
            ),
            Permission(
                0,
                "jointConclusion",
                "Лабораторное заключение",
            ),
            Permission(
                0,
                "workConclusion",
                "Лабораторное заключение по СМР",
            ),
            Permission(
                0,
                "stoTestAct",
                "Акт гидравлического испытания на прочность",
            ),
            Permission(
                0,
                "tubeLineDryingPermit",
                "Разрешение на проведение осушки полости трубопроводов",
            ),
            Permission(
                0,
                "tubeLineDryingAct",
                "Акт осушки полости магистрального газопровода",
            ),
            Permission(
                0,
                "nitrogenFillingAct",
                "Акт заполнения азотом полости магистрального газопровода",
            ),
            Permission(
                0,
                "installedEquipmentAct",
                "Акт смонтированного оборудования",
            ),
            Permission(
                0,
                "staffOrder",
                "Приказ о назначении",
            ),
            Permission(
                0,
                "fastenerPart",
                "Крепеж",
            ),
            Permission(
                0,
                "coatingMaterial",
                "Материал покрытия",
            ),
            Permission(
                0,
                "equipment",
                "Оборудование",
            ),
            Permission(
                0,
                "weldingConsumable",
                "Сварочные материалы",
            ),
            Permission(
                0,
                "thermalInsulation",
                "Теплоизоляционный материал",
            ),
            Permission(
                0,
                "waybill",
                "Транспортная накладная",
            ),
            Permission(
                0,
                "projectChangelog",
                "Ведомость изменений проекта",
            ),
            Permission(
                0,
                "parse",
                "Импорт данных"
            ),
            Permission(
                0,
                "chiefWelderCertificate",
                "Удостоверение руководителя сварочных работ"
            ),
            Permission(
                0,
                "defectoscopistCertificate",
                "Удостоверение дефектоскописта"
            ),
            Permission(
                0,
                "fitterCertificate",
                "Удостоверение монтажника высокопрочных болтов"
            ),
            Permission(
                0,
                "heatTreaterCertificate",
                "Удостоверение оператора-термиста"
            ),
            Permission(
                0,
                "receipt",
                "Поступление"
            ),
            Permission(
                0,
                "singleDocument",
                "Одиночный документ"
            ),
            Permission(
                0,
                "approvedDocuments",
                "ПСД"
            ),
            Permission(
                0,
                "extraDocument",
                "Пользовательский документ"
            )
        )
    )
}