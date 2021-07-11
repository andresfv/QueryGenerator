package com.queryGenerator.QueryGenerator.model;

import com.queryGenerator.QueryGenerator.service.ConsultaGenericaService;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
import cr.ac.una.cgi.sigesa.epf.atv.domain.VistaMovimientoContableActivoFijo;
import java.util.HashMap;
import java.util.Map;
import javax.faces.application.FacesMessage;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

@Named
public class ConsultaGenericaBean {


    @Autowired
    ConsultaGenericaService consultaGenericaService;

    List<VistaMovimientoContableActivoFijo> listaMovimientosContablesActivoFijo;
    FacesMessage message;

    private LazyDataModel<VistaMovimientoContableActivoFijo> lazyModel;

    String consultaHQL;

    Map<String, Object> listaParametros = new HashMap<String, Object>();

    @PostConstruct
    public void init() {
        listaMovimientosContablesActivoFijo = new ArrayList<VistaMovimientoContableActivoFijo>();

        consultaHQL = "select new cr.ac.una.cgi.sigesa.epf.atv.domain.VistaMovimientoContableActivoFijo ( \n"
                + "vmc.fechaAplicacion, \n"
                + "vmc.numeroAsiento, \n"
                + "vmc.origenTransaccion, \n"
                + "vmc.referencia, \n"
                + "vmc.concepto, \n"
                + "vmc.periodoMensual, \n"
                + "vmc.periodoAnual, \n"
                + "vmc.activo, \n"
                + "vmc.montoUnitario, \n"
                + "vmc.montoUnitarioExtranjero, \n"
                + "vmc.debe,\n"
                + "vmc.haber,\n"
                + "vmc.debeExtranjera,\n"
                + "vmc.haberExtranjera,\n"
                + "vmc.recepcion, \n"
                + "vmc.ordenCompra, \n"
                + "vmc.registroBancario) \n"
                + "from  VistaMovimientoContableActivoFijo   vmc\n";
//                + "where vmc.cuentaContable = :cuentaContable\n"
//                + "and vmc.fechaAplicacion >= :fechaDesde \n"
//                + "and vmc.fechaAplicacion <= :fechaHasta";
    }


    public List<VistaMovimientoContableActivoFijo> getListaMovimientosContablesActivoFijo() {
        return listaMovimientosContablesActivoFijo;
    }

    public void setListaMovimientosContablesActivoFijo(List<VistaMovimientoContableActivoFijo> listaMovimientosContablesActivoFijo) {
        this.listaMovimientosContablesActivoFijo = listaMovimientosContablesActivoFijo;
    }

    public LazyDataModel<VistaMovimientoContableActivoFijo> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<VistaMovimientoContableActivoFijo> lazyModel) {
        this.lazyModel = lazyModel;
    }


    @PostConstruct
    public void lazyLoad() {
        lazyModel = new LazyDataModel<VistaMovimientoContableActivoFijo>() {
            @Override
            public List<VistaMovimientoContableActivoFijo> load(int offset, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {

                long rowCount = consultaGenericaService.countVistaMovimientoContableActivoFijo(filterBy, consultaHQL);

                List<VistaMovimientoContableActivoFijo> movimientosContables = consultaGenericaService.findVistaMovimientoContableActivoFijo(offset, pageSize, sortBy, filterBy, consultaHQL, listaParametros);

//                long rowCount = movimientosContables.size() < pageSize ? offset + movimientosContables.size() : offset + pageSize + 1;
                // Setea el número total de filas de la consulta para establecer la ultima pagina
                setRowCount((int) rowCount);

                return movimientosContables;
            }
        };
    }
}
