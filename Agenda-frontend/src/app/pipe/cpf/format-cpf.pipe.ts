import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'formatCpf',
  standalone: false,
})
export class FormatCpfPipe implements PipeTransform {
  transform(cpf: string): string {
    if (!cpf) return '';

    const cleaned = cpf.replace(/\D/g, '');

    if (cleaned.length === 11) {
      return `${cleaned.substring(0, 3)}.${cleaned.substring(
        3,
        6
      )}.${cleaned.substring(6, 9)}-${cleaned.substring(9)}`;
    }

    return cpf;
  }
}
