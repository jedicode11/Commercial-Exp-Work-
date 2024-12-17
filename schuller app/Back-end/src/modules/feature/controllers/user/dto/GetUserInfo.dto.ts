import { IsString } from 'class-validator';

export class GetUserInfoDto {
  // @IsString()
  // readonly ticket: string;

  @IsString()
  user_id: string;
}
