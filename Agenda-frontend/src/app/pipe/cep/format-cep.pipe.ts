import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'formatCep',
  standalone: false,
})
export class FormatCepPipe implements PipeTransform {
  transform(cep: string): string {
    if (!cep) return '';

    const cleaned = cep.replace(/\D/g, '');

    if (cleaned.length === 8) {
      return `${cleaned.substring(0, 2)}.${cleaned.substring(
        2,
        5
      )}-${cleaned.substring(5)}`;
    }

    return cep;
  }
}
