package com.example.esercizioapi.data

interface RicercaDati {
    fun GetID() : String
}

class AlberoRicerca(
    private val lista : List<RicercaDati>
) {
    private val padre = Nodo<RicercaDati>()

    private fun inizializza() {
        for(element in lista){
            padre.inserisci(element.GetID(), element)
        }
    }

    fun search(key: String) : List<RicercaDati> {
        return padre.get(key)
    }


    fun dfs(Pezzo : String) : List<RicercaDati> {
        var node = padre.getNode(Pezzo)
        val coda = ArrayDeque<Nodo<RicercaDati>>()

        var lista : List<RicercaDati> = listOf()

        if(node == null)
            return listOf()

        for(child in node.child){
            if(child != null)
                coda.add(child)
        }

        lista = lista + node.data

        while(!coda.isEmpty()){
            node = coda.first()
            coda.removeFirst()

            lista = lista + node.data

            for(child in node.child){
                if(child != null)
                    coda.add(child)
            }
        }

        return lista
    }

    init{
        inizializza()
    }
}

class Nodo<T>(){
    var child : MutableList<Nodo<T>?> = MutableList(26) {null}
    var data : List<T> = mutableListOf<T>()

    fun inserisci(key : String, value : T){
        if(key == ""){
            data = data + value
            return
        }

        val carattere = key[0].code - 'a'.code

        if(carattere < 0) {
            inserisci(key.drop(1), value)
            return
        }

        if(child[carattere] == null)
            child[carattere] = Nodo<T>()
        child[carattere]?.inserisci(key.drop(1), value)
    }

    fun getNode(key: String) : Nodo<T>? {
        if(key.count() == 0)
            return this

        val carattere = key[0].code - 'a'.code

        if(carattere < 0) {
            return getNode(key.drop(1))
        }

        if(child[carattere] == null)
            return null
        return child[carattere]!!.getNode(key.drop(1))
    }

    fun get(key : String) : List<T> {
        if(key == ""){
            return data
        }

        val carattere = key[0].code - 'a'.code

        if(carattere < 0) {
            return get(key.drop(1))
        }

        if(child[carattere] == null)
            return listOf()
        return child[carattere]!!.get(key.drop(1))
    }
}