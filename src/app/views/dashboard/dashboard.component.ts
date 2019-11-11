import { Component, OnInit } from '@angular/core';

import * as Highcharts from 'highcharts';
import highcharts3D from 'highcharts/highcharts-3d.src';
import {DataSetService} from "../../services/data-set.service";
import {AnalysisResult, Clazz} from "../../models/clazz";
highcharts3D(Highcharts);

declare var require: any;
let Boost = require('highcharts/modules/boost');
let noData = require('highcharts/modules/no-data-to-display');
let More = require('highcharts/highcharts-more');

Boost(Highcharts);
noData(Highcharts);
More(Highcharts);
noData(Highcharts);

@Component({
  templateUrl: 'dashboard.component.html'
})
export class DashboardComponent implements OnInit {

  public analysisResult: AnalysisResult;
  public attributes: number[];
  public plottedAttributes = { x: 0, y: 1, z: 2 };

  public originalCharOptions: any = {
    title : { text: 'Dataset original' },
    series : [], // Los datos de la grafica en objetos { name: 'Name', data: [[1, 6, 5], [8, 7, 9]] }
    xAxis: { min:-2, max:2 },
    yAxis: { min:-2, max:2 },
    zAxis: { min:-2, max:2 },
    chart: {
      type: 'scatter',
      marginBottom: 100,
      marginRight: 50,
      options3d: {
        enabled: true,
        alpha: 20,
        beta: 30,
        depth: 750,
        viewDistance: 7,
        frame:{
          bottom :{
            size: 1,
            color: 'rgba(0, 0, 0, 0.02)'
          },
          back :{
            size: 1,
            color: 'rgba(0, 0, 0, 0.04)'
          },
          side :{
            size: 1,
            color: 'rgba(0, 0, 0, 0.06)'
          }
        }
      }
    },
  };
  public suavizadaChartOptions: any = {
    title : { text: 'Dataset suavizado' },
    series : [], // Los datos de la grafica en objetos { name: 'Name', data: [[1, 6, 5], [8, 7, 9]] }
    xAxis: { min:-2, max:2 },
    yAxis: { min:-2, max:2 },
    zAxis: { min:-2, max:2 },
    chart: {
      type: 'scatter',
      marginBottom: 100,
      marginRight: 50,
      options3d: {
        enabled: true,
        alpha: 20,
        beta: 30,
        depth: 750,
        viewDistance: 7,
        frame:{
          bottom :{
            size: 1,
            color: 'rgba(0, 0, 0, 0.02)'
          },
          back :{
            size: 1,
            color: 'rgba(0, 0, 0, 0.04)'
          },
          side :{
            size: 1,
            color: 'rgba(0, 0, 0, 0.06)'
          }
        }
      }
    },
  };

  constructor(private dataSetService: DataSetService) {}

  ngOnInit(): void {
    this.dataSetService
      .performAnalysis('seg-data.txt')
      .subscribe(
        result => {
          console.log('Se ha descargado el resultado del analisis');
          console.log(result);
          this.analysisResult = result;
          this.analysisResult.treeEvaluation = this.analysisResult.treeEvaluation.replace(/\n/gi, '<br>');
          this.analysisResult.bayesEvaluation = this.analysisResult.bayesEvaluation.replace(/\n/gi, '<br>');
          this.analysisResult.suavizadoBayesEvaluation = this.analysisResult.suavizadoBayesEvaluation.replace(/\n/gi, '<br>');
          this.analysisResult.suavizadoTreeEvaluation = this.analysisResult.suavizadoTreeEvaluation.replace(/\n/gi, '<br>');

          this.asignarAtributos(result.original.attributeSize);
          this.dibujarGrafica()
        },
        error => {
          console.log(`Ocurrio un error al descargar las clases`);
          console.log(error)
        }
      );
  }

  /**
   * Asigna la lista con los nombres de los atributos disponibles a graficar.
   *
   */
  private asignarAtributos(n: number) {
    this.attributes = [];
    for (let i = 0; i < n; i++) this.attributes.push(i);
  }

  /**
   * Dibuja las graficas scatter plot con el contenido del resultado del analisis
   * obtenido de la API, con los atributos a graficar especificados.
   */
  private dibujarGrafica() {
    this.originalCharOptions.series = [];
    this.analysisResult.original.classes.forEach(c =>
      this.originalCharOptions.series.push({
        name: c.name,
        data: c.data.map(a => [
          a[this.plottedAttributes.x],
          a[this.plottedAttributes.y],
          a[this.plottedAttributes.z]
        ])
      })
    );
    this.suavizadaChartOptions.series = [];
    this.analysisResult.suavizado.classes.forEach(c =>
      this.suavizadaChartOptions.series.push({
        name: c.name,
        data: c.data.map(a => [
          a[this.plottedAttributes.x],
          a[this.plottedAttributes.y],
          a[this.plottedAttributes.z]
        ])
      })
    );
    this.drawHighcharts();
  }

  /**
   * Configura las Highcharts y los eventos de movimiento y las renderiza en la vista.
   */
  private drawHighcharts() {
    console.log("Opciones de grafica original");
    console.log(this.originalCharOptions);
    let originalChart = Highcharts.chart('container', this.originalCharOptions);
    let dragStart = eStart => {
      eStart = originalChart.pointer.normalize(eStart);

      let posX = eStart.chartX,
        posY = eStart.chartY,
        alpha = originalChart.options.chart.options3d.alpha,
        beta = originalChart.options.chart.options3d.beta,
        sensitivity = 5,  // lower is more sensitive
        handlers = [];

      let drag = (e) => {
        e = originalChart.pointer.normalize(e); // Get e.chartX and e.chartY
        originalChart.update({
          chart: {
            options3d: {
              alpha: alpha + (e.chartY - posY) / sensitivity,
              beta: beta + (posX - e.chartX) / sensitivity
            }
          }
        }, undefined, undefined, false);
      };
      handlers.push(Highcharts.addEvent(document, 'mousemove', drag));
      handlers.push(Highcharts.addEvent(document, 'touchmove', drag));
      let unbindAll = () => {
        handlers.forEach(function (unbind) {if (unbind) {unbind();}});
        handlers.length = 0;
      };
      handlers.push(Highcharts.addEvent(document, 'mouseup', unbindAll));
      handlers.push(Highcharts.addEvent(document, 'touchend', unbindAll));
    };
    Highcharts.addEvent(originalChart.container, 'mousedown', dragStart);
    Highcharts.addEvent(originalChart.container, 'touchstart', dragStart);

    console.log("Opciones de grafica suavizada");
    console.log(this.suavizadaChartOptions);
    let softChart = Highcharts.chart('container2', this.suavizadaChartOptions);
    let dragStart2 = eStart => {
      eStart = softChart.pointer.normalize(eStart);

      let posX = eStart.chartX,
        posY = eStart.chartY,
        alpha = originalChart.options.chart.options3d.alpha,
        beta = originalChart.options.chart.options3d.beta,
        sensitivity = 5,  // lower is more sensitive
        handlers = [];

      let drag = (e) => {
        e = softChart.pointer.normalize(e); // Get e.chartX and e.chartY
        softChart.update({
          chart: {
            options3d: {
              alpha: alpha + (e.chartY - posY) / sensitivity,
              beta: beta + (posX - e.chartX) / sensitivity
            }
          }
        }, undefined, undefined, false);
      };
      handlers.push(Highcharts.addEvent(document, 'mousemove', drag));
      handlers.push(Highcharts.addEvent(document, 'touchmove', drag));
      let unbindAll = () => {
        handlers.forEach(function (unbind) {if (unbind) {unbind();}});
        handlers.length = 0;
      };
      handlers.push(Highcharts.addEvent(document, 'mouseup', unbindAll));
      handlers.push(Highcharts.addEvent(document, 'touchend', unbindAll));
    };
    Highcharts.addEvent(originalChart.container, 'mousedown', dragStart2);
    Highcharts.addEvent(originalChart.container, 'touchstart', dragStart2);
  }
}
