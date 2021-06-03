package com.example.celulareport.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

//BOJO to retrieve information from reports and store ou show in somewhere
@Entity(tableName = "report_table")
data class ReportEntity(

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    @ColumnInfo(name = "is_favorite")
    var isFavorite: Boolean =false,

    @ColumnInfo(name = "celula")
    var nomeCelula: String,

    @ColumnInfo(name = "lider")
    var nomeLider: String,

    @ColumnInfo(name = "colider")
    var nomeColider: String,

    @ColumnInfo(name = "anfitriao")
    var nomeAnfitriao: String,

    @ColumnInfo(name = "data")
    private var dataReuniao: Date,

    @ColumnInfo(name = "n_membros")
    var numMembros: String,

    @ColumnInfo(name = "n_visitantes")
    var numVisitantes: String,

    var oferta: String,

    var comentarios: String,

    ){

    override fun toString(): String {
        return dataReuniao.toString()
    }
}