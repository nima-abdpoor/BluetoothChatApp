package com.nima.bluetoothchatapp.mapper

interface EntityMapper<EntityMapper,DomainModel> {
    fun mapFromEntity(entity: EntityMapper): DomainModel?
    fun mapToEntity(domainModel: DomainModel): EntityMapper?
}