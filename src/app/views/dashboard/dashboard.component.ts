import { Component, OnInit } from '@angular/core';

import * as Highcharts from 'highcharts';
import highcharts3D from 'highcharts/highcharts-3d.src';
import {DataSetService} from "../../services/data-set.service";
import {Clazz} from "../../models/clazz";
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

  public options: any = {
    chart: {
      type: 'scatter',
      marginBottom: 100,
      marginRight: 50,
      options3d: {
        enabled: true,
        alpha: 20,
        beta: 30,
        depth: 650,
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
    title : {
      text: '3D Scatter Plot'
    },
    xAxis:{
      min:0,
      max:10
    },
    yAxis:{
      min:0,
      max:10
    },
    zAxis:{
      min:0,
      ax:10
    },
    series : []
    /*[{
      name: 'Reading',
      data: [[1, 6, 5], [8, 7, 9]]
    }]*/
  };

  public clases: Clazz[];
  public attributes: number[];

  public xAxis = 0;
  public yAxis = 1;
  public zAxis = 2;

  constructor(private dataSetService: DataSetService) {}

  ngOnInit(): void {
    this.dataSetService
      .getClasses('seg-data.txt')
      .subscribe(
        clases => {
          console.log('Se han descargado las clases');
          console.log(clases);
          this.clases = clases;
          this.asignarAtributos(clases.pop().attributeSize);
          this.dibujarGrafica()
        },
        error => {
          console.log(`Ocurrio un error al descargar las clases`);
          console.log(error)
        });
  }

  private asignarAtributos(n: number) {
    this.attributes = [];
    for (let i = 0; i < n; i++) this.attributes.push(i);
  }

  private dibujarGrafica() {
    /* Convierte el conjunto de clases descargado desde la API, a una estructura
    para el componente del scatter plot, de acuerdo a la seleccion de atributos
    a graficar*/
    console.log(this.xAxis);
    console.log(this.yAxis);
    console.log(this.zAxis);
    this.options.series = [];
    this.clases.forEach(c => this.options.series.push({
      name: c.name,
      data: c.data.map(a => [a[this.xAxis], a[this.yAxis], a[this.zAxis]])
    }));

    // Dibuja la grafica
    let chart = Highcharts.chart('container', this.options);
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
