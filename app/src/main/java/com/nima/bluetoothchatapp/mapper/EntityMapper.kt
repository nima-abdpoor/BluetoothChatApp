package com.nima.bluetoothchatapp.mapper

import com.nima.bluetoothchatapp.database.entities.ChatMessage

interface EntityMapper<EntityMapper,DomainModel> {
    fun mapFromEntity(entity: EntityMapper): DomainModel?
    fun mapToEntity(domainModel: DomainModel): ChatMessage?
}