import {Injectable} from '@angular/core'
import {HttpClient} from "@angular/common/http";
import {Clazz} from "../models/Clazz";
import {environment} from "../../environments/environment";

@Injectable({providedIn:'root'})
export class DataSetService {
  constructor(private httpClient: HttpClient) {}

  getClasses(id: string) {
    return this.httpClient
      .get<Array<Clazz>>(`${environment.apiUrl}/dataset/${id}/classes`)
  }
}
