import {Injectable} from '@angular/core'
import {HttpClient} from "@angular/common/http";
import {AnalysisResult, Clazz} from "../models/clazz";
import {environment} from "../../environments/environment";

@Injectable({providedIn:'root'})
export class DataSetService {
  constructor(private httpClient: HttpClient) {}

  performAnalysis(id: string) {
    return this.httpClient
      .get<AnalysisResult>(
        `${environment.apiUrl}/dataset/${id}/performAnalysis`,
        {
          params: {
            'normalizacion': 'Z_SCORE'
          }
        })
  }
}
