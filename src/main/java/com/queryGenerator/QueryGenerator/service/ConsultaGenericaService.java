/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.queryGenerator.QueryGenerator.service;

import com.queryGenerator.QueryGenerator.entity.EstadoActivoFijo;
import com.queryGenerator.QueryGenerator.entity.VistaMovimientoContableActivoFijo;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;

/**
 *
 * @author Luis Andrés Fallas Valenciano
 */
public interface ConsultaGenericaService {

    /**
     * Realiza un consulta de hql basica.
     *
     * @param consultaHql
     * @return lista de resultados sin ningún filtro.
     */
    public List<Object> getResultadosConsulta(String consultaHql);

    /**
     * Realiza una consulta de hql paginada desde base de datos.
     *
     * @param pageNumber
     * @param pageSize
     * @param sort
     * @param filterBy
     * @param consultaHql
     * @param listaParametros
     * @return lista de resultados filtrada por pagina y ordenada segun
     * parametro.
     */
    public List<Object> getResultadosConsulta(int pageNumber, int pageSize, Map<String, SortMeta> sort, Map<String, FilterMeta> filterBy, Map<String, Object> listaFragmentosHql, Map<String, Object> listaParametros);

    public long countResultadosConsulta(Map<String, FilterMeta> filterBy, Map<String, Object> listaFragmentosHql, Map<String, Object> listaParametros);

    /**
     * Metodo que divide la consulta ingresada en partes por "select", "where",
     * "groupBy" y "sortBy"
     *
     * @param consultaHql
     * @return Map<String, String> donde se almacena cada parte de la consulta
     * con las llaves "select", "where", "groupBy" y "sortBy".
     * @throws Exception
     */
    public Map<String, Object> fragmentaConsultaHql(StringBuilder consultaHql) throws Exception;
}
