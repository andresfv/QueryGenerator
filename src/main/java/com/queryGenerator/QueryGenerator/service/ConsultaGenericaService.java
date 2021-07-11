/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.queryGenerator.QueryGenerator.service;

import cr.ac.una.cgi.sigesa.epf.atv.domain.VistaMovimientoContableActivoFijo;
import java.util.List;
import java.util.Map;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;

/**
 *
 * @author Luis Andrés Fallas Valenciano
 */
public interface ConsultaGenericaService {

    public List<VistaMovimientoContableActivoFijo> findVistaMovimientoContableActivoFijo(int pageNumber, int pageSize, Map<String, SortMeta> sort, Map<String, FilterMeta> filterBy, String consultaHql, Map<String, Object> listaParametros);

    public long countVistaMovimientoContableActivoFijo(Map<String, FilterMeta> filterBy, String consultaHql);

}
