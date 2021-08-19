package com.queryGenerator.QueryGenerator.model;

import com.queryGenerator.QueryGenerator.service.ConsultaGenericaService;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

@Named
public class ConsultaGenericaBean {

    @Autowired
    ConsultaGenericaService consultaGenericaService;

    private List<Object> dataSource;
    private List<Object> listaResultadosConsulta;
    private FacesMessage message;

    private LazyDataModel<Object> lazyModel;
    private Map<String, Object> fragmentosHql = new HashMap<String, Object>();

    private boolean contar; //Establece si se debe ejecutar query de conteo

    private long rowCount;

    private static final Logger logger = Logger.getLogger(ConsultaGenericaBean.class.getName());

    String consultaHQL = "select new com.queryGenerator.QueryGenerator.entity.VistaMovimientoContableActivoFijo (\n"
            + "                vmc.fechaAplicacion as fechaAplicacion, \n"
            + "                vmc.numeroAsiento as numeroAsiento)\n"
            + "                from  VistaMovimientoContableActivoFijo vmc\n"
            + "                group by vmc.fechaAplicacion, \n"
            + "                vmc.numeroAsiento";

//    String consultaHQL = "select cat.id as id, (select max (kit.weight)"
//            + " from cat.kitten kit where kit.weight = 100) as weigth"
//            + " from Cat as cat"
//            + " where cat.name = some (select name.nickName from Name as name)"
//            + " group by cat.name, cat.id"
//            + " sort by cat.name";
    int tipoConsulta = 1;

    Map<String, Object> listaParametros = new HashMap<String, Object>();

    @PostConstruct
    public void init() {
        dataSource = new ArrayList<Object>();
        listaResultadosConsulta = new ArrayList<Object>();
        contar = true;

        try {
            fragmentosHql = consultaGenericaService.fragmentaConsultaHql(new StringBuilder(consultaHQL));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        if (tipoConsulta == 1) {
            listaResultadosConsulta = consultaGenericaService.getResultadosConsulta(consultaHQL);
        }
    }

    @PostConstruct
    public void lazyLoad() {
        lazyModel = new LazyDataModel<Object>() {
            @Override
            public List<Object> load(int offset, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
                List<Object> resultadosConsulta = new ArrayList<Object>();
                try {
                    if (contar) {
                        rowCount = consultaGenericaService.countResultadosConsulta(filterBy, fragmentosHql, listaParametros);
                        contar = false;
                    }

                    resultadosConsulta = consultaGenericaService.getResultadosConsulta(offset, pageSize, sortBy, filterBy, fragmentosHql, listaParametros);

                    // rowCount = resultadosConsulta.size() < pageSize ? offset + resultadosConsulta.size() : offset + pageSize + 1;
                    setRowCount((int) rowCount);// Setea el número total de filas de la consulta para establecer la ultima pagina

                } catch (Exception e) {
                    logger.log(Level.SEVERE, "ERROR CAUSADO POR: " + e.getMessage());
                }

                return resultadosConsulta;

            }
        };
    }

    public int getTipoConsulta() {
        return tipoConsulta;
    }

    public void setTipoConsulta(int tipoConsulta) {
        this.tipoConsulta = tipoConsulta;
    }

    public String getConsultaHQL() {
        return consultaHQL;
    }

    public void setConsultaHQL(String consultaHQL) {
        this.consultaHQL = consultaHQL;
    }

    public List<Object> getListaResultadosConsulta() {
        return listaResultadosConsulta;
    }

    public void setListaResultadosConsulta(List<Object> listaResultadosConsulta) {
        this.listaResultadosConsulta = listaResultadosConsulta;
    }

    public LazyDataModel<Object> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<Object> lazyModel) {
        this.lazyModel = lazyModel;
    }

    /**
     * Permite indicar que se debe volver a contar ya que se adicionaron filtros
     * por lo que se debe modificar el total de paginas.
     *
     * @param filterEvent
     */
    public void filterListener(FilterEvent filterEvent) {
        contar = true;
    }
}
