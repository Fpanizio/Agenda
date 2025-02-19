import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'formatCnpj',
  standalone: false,
})
export class FormatCnpjPipe implements PipeTransform {
  transform(cnpj: string): string {
    if (!cnpj) return '';
    return cnpj.replace(
      /(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/,
      '$1.$2.$3/$4-$5'
    );
  }
}
