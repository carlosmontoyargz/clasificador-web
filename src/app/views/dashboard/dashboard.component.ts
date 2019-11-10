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

  public analysisResult: AnalysisResult;
  public attributes: number[];
  public plottedAttributes = { x: 0, y: 1, z: 2 };

  constructor(private dataSetService: DataSetService) {}

  ngOnInit(): void {
    this.dataSetService
      .performAnalysis('seg-data.txt')
      .subscribe(
        result => {
          console.log('Se ha descargado el resultado del analisis');
          console.log(result);
          this.analysisResult = result;
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
    console.log(`X axis: ${this.plottedAttributes.x}, Y axis: ${this.plottedAttributes.y}, Z axis: ${this.plottedAttributes.z}`);
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
    this.drawHighcharts();
  }

  /**
   * Configura las Highcharts y los eventos de movimiento y las renderiza en la vista.
   */
  private drawHighcharts() {
    console.log("Original chart options");
    console.log(this.originalCharOptions);
    let chart = Highcharts.chart('container', this.originalCharOptions);
    let dragStart = eStart => {
      eStart = chart.pointer.normalize(eStart);

      let posX = eStart.chartX,
        posY = eStart.chartY,
        alpha = chart.options.chart.options3d.alpha,
        beta = chart.options.chart.options3d.beta,
        sensitivity = 4,  // lower is more sensitive
        handlers = [];

      function drag(e) {
        // Get e.chartX and e.chartY
        e = chart.pointer.normalize(e);

        chart.update({
          chart: {
            options3d: {
              alpha: alpha + (e.chartY - posY) / sensitivity,
              beta: beta + (posX - e.chartX) / sensitivity
            }
          }
        }, undefined, undefined, false);
      }
      handlers.push(Highcharts.addEvent(document, 'mousemove', drag));
      handlers.push(Highcharts.addEvent(document, 'touchmove', drag));
      function unbindAll() {
        handlers.forEach(function (unbind) {if (unbind) {unbind();}});
        handlers.length = 0;
      }
      handlers.push(Highcharts.addEvent(document, 'mouseup', unbindAll));
      handlers.push(Highcharts.addEvent(document, 'touchend', unbindAll));
    };
    Highcharts.addEvent(chart.container, 'mousedown', dragStart);
    Highcharts.addEvent(chart.container, 'touchstart', dragStart);
  }
}
