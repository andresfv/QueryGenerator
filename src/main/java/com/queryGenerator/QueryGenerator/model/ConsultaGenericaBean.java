package com.queryGenerator.QueryGenerator.model;

import com.queryGenerator.QueryGenerator.entity.EstadoActivoFijo;
import com.queryGenerator.QueryGenerator.entity.VistaMovimientoContableActivoFijo;
import com.queryGenerator.QueryGenerator.service.ConsultaGenericaService;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
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

    List<VistaMovimientoContableActivoFijo> listaResultadosConsulta;
    FacesMessage message;

    private LazyDataModel<VistaMovimientoContableActivoFijo> lazyModel;

    String consultaHQL;

    Map<String, Object> listaParametros = new HashMap<String, Object>();

    @PostConstruct
    public void init() {
        listaResultadosConsulta = new ArrayList<VistaMovimientoContableActivoFijo>();

        consultaHQL = "select new com.queryGenerator.QueryGenerator.entity.VistaMovimientoContableActivoFijo ( \n"
                + "vmc.fechaAplicacion, \n"
                + "vmc.numeroAsiento) \n"
                + "from  VistaMovimientoContableActivoFijo   vmc\n";
//                + "where vmc.cuentaContable = :cuentaContable\n"
//                + "and vmc.fechaAplicacion >= :fechaDesde \n"
//                + "and vmc.fechaAplicacion <= :fechaHasta";

//        consultaHQL = "select ea from EstadoActivoFijo ea";
    }

    public List<VistaMovimientoContableActivoFijo> getListaResultadosConsulta() {
        return listaResultadosConsulta;
    }

    public void setListaResultadosConsulta(List<VistaMovimientoContableActivoFijo> listaResultadosConsulta) {
        this.listaResultadosConsulta = listaResultadosConsulta;
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

//                long rowCount = consultaGenericaService.countResultadosConsulta(filterBy, "select count(vmc.numeroAsiento) from  VistaMovimientoContableActivoFijo vmc");

                List<VistaMovimientoContableActivoFijo> resultadosConsulta = consultaGenericaService.getResultadosConsulta(offset, pageSize, sortBy, filterBy, consultaHQL, listaParametros);

                long rowCount = resultadosConsulta.size() < pageSize ? offset + resultadosConsulta.size() : offset + pageSize + 1;
                setRowCount((int) rowCount);// Setea el número total de filas de la consulta para establecer la ultima pagina

                return resultadosConsulta;
            }
        };
    }
}
