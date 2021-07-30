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
    public List<Object> getResultadosConsulta(int pageNumber, int pageSize, Map<String, SortMeta> sort, Map<String, FilterMeta> filterBy, String consultaHql, Map<String, Object> listaParametros);

    public long countResultadosConsulta(Map<String, FilterMeta> filterBy, String consultaHql, Map<String, Object> listaParametros);

    public Map<String, String> fragmentaConsultaHql(StringBuilder consultaHql);
}
