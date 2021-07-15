/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.queryGenerator.QueryGenerator.service;

import com.queryGenerator.QueryGenerator.entity.EstadoActivoFijo;
import com.queryGenerator.QueryGenerator.entity.VistaMovimientoContableActivoFijo;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.MatchMode;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
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
    public List<VistaMovimientoContableActivoFijo> getResultadosConsulta(int pageNumber, int pageSize, Map<String, SortMeta> sort, Map<String, FilterMeta> filterBy, String consultaHql, Map<String, Object> listaParametros) {

///Para crear el query de forma generica se puede dividir en 4 partes que serían: select from, where, group by, order by
        StringBuilder selectQuery = new StringBuilder(consultaHql);
        StringBuilder whereQuery = new StringBuilder();
        StringBuilder groupByQuery = new StringBuilder();
        StringBuilder orderByQuery = new StringBuilder();


        //ORDENAMIENTO POR DATATABLE
        if (sort != null && sort.size() > 0) {
            orderByQuery = new StringBuilder(ordenarDataTable(sort, orderByQuery));
        }

        //FILTRADO POR DATATABLE
        if (filterBy != null && filterBy.size() > 0) {
            whereQuery = new StringBuilder(filtrarDataTable(filterBy, whereQuery, listaParametros));
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

            List<VistaMovimientoContableActivoFijo> listaMovimientosContables = query.getResultList();

            return listaMovimientosContables;
        }
        return null;
    }

    @Override
    public long countResultadosConsulta(Map<String, FilterMeta> filterBy, String consultaHql) {
        ///Para crear el query de forma generica se puede dividir en 4 partes que serían: select from, where, group by, order by
        StringBuilder selectQuery = new StringBuilder(consultaHql);
        StringBuilder whereQuery = new StringBuilder();
        StringBuilder groupByQuery = new StringBuilder();
        Map<String, Object> listaParametros = new HashMap<String, Object>();

        //FILTRADO POR DATATABLE
        if (filterBy != null && filterBy.size() > 0) {
            whereQuery = new StringBuilder(filtrarDataTable(filterBy, whereQuery, listaParametros));
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
                if (filterMeta.getFilterMatchMode().equals(MatchMode.STARTS_WITH) || filterMeta.getFilterMatchMode().equals(MatchMode.CONTAINS)) {
                    listaParametros.put(filterMeta.getFilterField(), "%" + filterMeta.getFilterValue() + "%");
                    whereQuery.append(filterMeta.getFilterField() + " like " + ":" + filterMeta.getFilterField());

                } else if (filterMeta.getFilterMatchMode().equals(MatchMode.EXACT)) {
                    listaParametros.put(filterMeta.getFilterField(), filterMeta.getFilterValue());
                    whereQuery.append(filterMeta.getFilterField() + " = " + ":" + filterMeta.getFilterField());
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
//        listaMaterias.stream().sorted((x, y) -> x.getNombre().compareTo(y.getNombre())).forEach(System.out::println);
//        return listaMaterias.stream().sorted((x, y) -> x.getNombre().compareTo(y.getNombre())).collect(Collectors.toList());

        //Si viene un ordenamiento por columna en el dataTable se limpia el query orderBy dando prioridad al nuevo ordenamiento.
        orderByQuery = new StringBuilder();
        orderByQuery.append("order by ");

        for (SortMeta sortMeta : sort.values()) {
            orderByQuery.append(sortMeta.getSortField());

            if (sortMeta.getSortOrder().equals(SortOrder.ASCENDING)) {
                orderByQuery.append(" ASC");
            } else if (sortMeta.getSortOrder().equals(SortOrder.DESCENDING)) {
                orderByQuery.append(" DESC");
            }
        }
        return orderByQuery;
    }
}
