/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.queryGenerator.QueryGenerator.service;

import com.queryGenerator.QueryGenerator.entity.EstadoActivoFijo;
import com.queryGenerator.QueryGenerator.entity.VistaMovimientoContableActivoFijo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.MatchMode;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.primefaces.model.filter.FilterConstraint;
import org.primefaces.util.LocaleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Luis Andrés Fallas Valenciano
 */
@Transactional
@Service
public class ConsultaGenericaServiceImpl implements ConsultaGenericaService {

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<Object> getResultadosConsulta(String consultaHql) {
        List<Object> resultadosConsulta = new ArrayList<Object>();
        if (consultaHql != null && consultaHql.length() > 0) {
            Query query = entityManager.createQuery(consultaHql);
            resultadosConsulta = query.getResultList();
        }
        return resultadosConsulta;
    }

    @Override
    public List<Object> getResultadosConsulta(int pageNumber, int pageSize, Map<String, SortMeta> sort, Map<String, FilterMeta> filterBy, String consultaHql, Map<String, Object> listaParametros) {

///Para crear el query de forma generica se puede dividir en 4 partes que serían: select from, where, group by, order by
        StringBuilder selectQuery = new StringBuilder(consultaHql);
        StringBuilder whereQuery = new StringBuilder();
        StringBuilder groupByQuery = new StringBuilder();
        StringBuilder orderByQuery = new StringBuilder();
        List<Object> resultadosConsulta = new ArrayList<Object>();

        //ORDENAMIENTO POR DATATABLE
        if (sort != null && sort.size() > 0) {
            orderByQuery = new StringBuilder(ordenarDataTable(sort, orderByQuery));
        }

        //FILTRADO POR DATATABLE
        if (filterBy != null && filterBy.size() > 0) {
            whereQuery = new StringBuilder(filtrarDataTable(filterBy, whereQuery, listaParametros));
        } else {
            listaParametros.clear();
        }

        //ARMADO DEL HQL
        if (selectQuery.length() > 0) {
            StringBuilder stringQuery = new StringBuilder();
            stringQuery.append(selectQuery);
            if (whereQuery.length() > 0) {
                stringQuery.append(whereQuery);
            }
            if (groupByQuery.length() > 0) {
                stringQuery.append(groupByQuery);
            }
            if (orderByQuery.length() > 0) {
                stringQuery.append(orderByQuery);
            }

            //EJECUCION DEL HQL
            Query query = entityManager.createQuery(stringQuery.toString());
            query.setFirstResult(pageNumber);
            query.setMaxResults(pageSize);

            for (Map.Entry<String, Object> parametro : listaParametros.entrySet()) {
                query.setParameter(parametro.getKey(), parametro.getValue());
            }

            resultadosConsulta = query.getResultList();

        }
        return resultadosConsulta;
    }

    @Override
    public long countResultadosConsulta(Map<String, FilterMeta> filterBy, String consultaHql, Map<String, Object> listaParametros) {
        StringBuilder selectQuery = new StringBuilder(consultaHql);
        StringBuilder whereQuery = new StringBuilder();
        StringBuilder groupByQuery = new StringBuilder();

        //FILTRADO POR DATATABLE
        if (filterBy != null && filterBy.size() > 0) {
            whereQuery = new StringBuilder(filtrarDataTable(filterBy, whereQuery, listaParametros));
        } else {
            listaParametros.clear();
        }

        //ARMADO DEL HQL
        if (selectQuery.length() > 0) {
            StringBuilder stringQuery = new StringBuilder();
            stringQuery.append(selectQuery);
            if (whereQuery.length() > 0) {
                stringQuery.append(whereQuery);
            }
            if (groupByQuery.length() > 0) {
                stringQuery.append(groupByQuery);
            }

            //EJECUCION DEL HQL
            Query query = entityManager.createQuery(stringQuery.toString());
            Iterator<Map.Entry<String, Object>> parametroIterador = listaParametros.entrySet().iterator();
            while (parametroIterador.hasNext()) {
                Map.Entry<String, Object> parametro = parametroIterador.next();
                query.setParameter(parametro.getKey(), parametro.getValue());
            }

            long countResult = (long) query.getSingleResult();
            return countResult;
        }
        return 0L;
    }

    /**
     * Filtra el dataTable según los valores digitados en el encabezado de las
     * columnas.
     *
     * @param filterBy
     * @param whereQuery
     * @return hql where
     */
    public StringBuilder filtrarDataTable(Map<String, FilterMeta> filterBy, StringBuilder whereQuery, Map<String, Object> listaParametros) {
//        listaMaterias.stream().filter(x -> x.getNombre().startsWith("C")).forEach(System.out::println);
//        listaMaterias.stream().filter(x -> x.getNombre().startsWith("C")).forEach(x -> System.out.println(x.getNombre()));

        for (FilterMeta filterMeta : filterBy.values()) {
            if (filterMeta.getFilterValue() != null) {
                if (whereQuery.length() == 0) {
                    whereQuery.append(" where ");
                } else {
                    whereQuery.append(" and ");
                }
                if (filterMeta.getMatchMode().equals(MatchMode.STARTS_WITH) || filterMeta.getMatchMode().equals(MatchMode.CONTAINS)) {
                    listaParametros.put(filterMeta.getField(), "%" + filterMeta.getFilterValue() + "%");
                    whereQuery.append(filterMeta.getField() + " like " + ":" + filterMeta.getField());

                } else if (filterMeta.getMatchMode().equals(MatchMode.EXACT)) {
                    listaParametros.put(filterMeta.getField(), filterMeta.getFilterValue());
                    whereQuery.append(filterMeta.getField() + " = " + ":" + filterMeta.getField());
                }
            }
        }

        return whereQuery;
    }

    /**
     * Ordena los registros del dataTable según la columna marcada para ordenar.
     *
     * @param listaMaterias
     * @return hql orderBy
     */
    public StringBuilder ordenarDataTable(Map<String, SortMeta> sort, StringBuilder orderByQuery) {

        //Si viene un ordenamiento por columna en el dataTable se limpia el query orderBy dando prioridad al nuevo ordenamiento.
        orderByQuery = new StringBuilder();
        orderByQuery.append("order by ");

        for (SortMeta sortMeta : sort.values()) {
            orderByQuery.append(sortMeta.getField());

            if (sortMeta.getOrder().equals(SortOrder.ASCENDING)) {
                orderByQuery.append(" ASC");
            } else if (sortMeta.getOrder().equals(SortOrder.DESCENDING)) {
                orderByQuery.append(" DESC");
            }
        }
        return orderByQuery;
    }

    /**
     * Metodo que divide la consulta ingresada en partes por "select", "where",
     * "groupBy" y "sortBy"
     *
     * @param consultaHql
     * @return Map<String, String> donde se almacena cada parte de la consulta
     * con las llaves "select", "where", "groupBy" y "sortBy".
     * @throws Exception
     */
    @Override
    public Map<String, String> fragmentaConsultaHql(StringBuilder consultaHql) throws Exception {
        Map<String, String> listaFragmentosHql = new HashMap<String, String>();

        //Primero se procede a obtener el indice donde comienza cada sección de la consulta hql fuera de parentesis para evitar confundir con una subconsulta.****************
        int indiceFrom = buscaIndicePalabraFueraParentesis(consultaHql, "from");
        int indiceWhere = buscaIndicePalabraFueraParentesis(consultaHql, "where");
        int indiceGroupBy = buscaIndicePalabraFueraParentesis(consultaHql, "group by");
        int indiceSortBy = buscaIndicePalabraFueraParentesis(consultaHql, "sort by");

        //Posteriormente se procede a separar cada parte de la consulta por select, where, groupBy y sortBy.*************************************************************
        StringBuilder selectQuery = new StringBuilder();
        StringBuilder whereQuery = new StringBuilder();
        StringBuilder groupByQuery = new StringBuilder();
        StringBuilder sortByQuery = new StringBuilder();

        //Separa la seccion select del hql
        if (indiceFrom != -1) {
             selectQuery.append(consultaHql.substring(0, indiceFrom - 1));
        } else {
            throw new Exception("La consulta no tiene from");
        }

        //Separa la seccion where del hql
        if (indiceWhere != -1) {
            if (indiceGroupBy == -1 && indiceSortBy == -1) {
                whereQuery.append(consultaHql.substring(indiceWhere - 1));
            } else {
                whereQuery.append(consultaHql.substring(indiceWhere - 1, indiceGroupBy != -1 ? indiceGroupBy : indiceSortBy));
            }
        }

        //Separa la seccion group by del hql
        if (indiceGroupBy != -1) {
            if (indiceSortBy == -1) {
                groupByQuery.append(consultaHql.substring(indiceGroupBy - 1));
            } else {
                groupByQuery.append(consultaHql.substring(indiceGroupBy - 1, indiceSortBy));
            }
        }

        //Separa la seccion sort by del hql
        if (indiceSortBy != -1) {
            sortByQuery.append(consultaHql.substring(indiceSortBy - 1));
        }

        //Finalmente se procede a llenar la variable de tipo map con las partes de la consulta.**************************************************************************************
        listaFragmentosHql.put("select", selectQuery.toString().trim());

        if (whereQuery.length() > 0) {
            listaFragmentosHql.put("where", whereQuery.toString().trim());
        }

        if (groupByQuery.length() > 0) {
            listaFragmentosHql.put("groupBy", groupByQuery.toString().trim());
        }

        if (sortByQuery.length() > 0) {
            listaFragmentosHql.put("sortBy", sortByQuery.toString().trim());
        }

        return listaFragmentosHql;
    }

    /**
     * Método que busca el indice de una palabra en la consulta hql siempre y
     * cuando esta esté fuera de un parentesis.
     *
     * @param consultaHql
     * @return indice de palabra, -1 en caso de no encontrarla
     * @throws Exception
     */
    public int buscaIndicePalabraFueraParentesis(StringBuilder consultaHql, String palabra) throws Exception {
        //Primero se buscan los indices donde se abren y cierran parentesis para evitar entorpecer la fragmentacion del hql--------------------------------------------------

        //Se usa StringBuilder ya que es mutable
        StringBuilder consultaHqlIndices = new StringBuilder(consultaHql);

        int contParentesisIzq = 0;
        int contParentesisDere = 0;

        //cuenta parentesis izquierdos y derechos
        for (int i = 0; i < consultaHqlIndices.length(); i++) {
            if (consultaHqlIndices.charAt(i) == '(') {
                contParentesisIzq++;
            } else if (consultaHqlIndices.charAt(i) == ')') {
                contParentesisDere++;
            }
        }

        //Verifica si los parentesis son coinciden en caso contrario el hql esta erroneo
        if (contParentesisIzq != contParentesisDere) {
            throw new Exception("La consulta tiene un parentesis sin cerrar");
        }

        //Busca parentesis izq y dere y va reemplazando '(' por '{' y ')' por '}' y repite este proceso por el número de parentesis izq
        int[][] indices = new int[contParentesisIzq][2];
        for (int z = 0; z < contParentesisIzq; z++) {
            for (int i = 0; i < consultaHqlIndices.length(); i++) {
                int indiceParentesisIzq = 0;
                int indiceParentesisDere = 0;
                if (consultaHqlIndices.charAt(i) == '(') {
                    indiceParentesisIzq = i;
                    for (int j = i + 1; j < consultaHqlIndices.length(); j++) {
                        if (consultaHqlIndices.charAt(j) == '(') {
                            indiceParentesisIzq = j;
                        } else if (consultaHqlIndices.charAt(j) == ')') {
                            indiceParentesisDere = j;
                            break;
                        }
                    }
                    indices[z][0] = indiceParentesisIzq;
                    indices[z][1] = indiceParentesisDere;
                    consultaHqlIndices.setCharAt(indiceParentesisIzq, '{');
                    consultaHqlIndices.setCharAt(indiceParentesisDere, '}');
                    break;
                }
            }
        }

        //Seguidamente se busca el from del hql verificando que no sea el de una subconsulta--------------------------------------------------------------------------------
        StringBuilder buscadorPalabra = new StringBuilder(consultaHql);
        boolean flag = true;
        int indexPalabra;

        do {
            indexPalabra = buscadorPalabra.indexOf(palabra);

            for (int i = 0; i < indices.length; i++) {
                if (indexPalabra == -1) {
                    flag = false;
                }

                //Verifica que la palabra buscada no este dentro de un parentesis ya que podria tratarse de una subconsulta
                if (indexPalabra >= indices[i][0] && indexPalabra <= indices[i][1]) {

                    //Remplaza la palabra encontrada por un token para no volver a ser encontrada
                    buscadorPalabra.replace(indexPalabra, indexPalabra + 1, "*");
                    flag = true;
                    break;
                } else {
                    flag = false;
                }
            }

        } while (flag);

        return indexPalabra;
    }
}
