import React from 'react';
import { MapPin, Monitor, Star, Clock } from 'lucide-react';
import { Card, CardContent, CardFooter } from '../ui/Card';
import { Avatar } from '../ui/Avatar';
import { Badge } from '../ui/Badge';
import { Button } from '../ui/Button';
import { TutoringOffer } from '../../types';
interface TutorCardProps {
  offer: TutoringOffer;
  onRequest: (offer: TutoringOffer) => void;
  isRequesting?: boolean;
}
export function TutorCard({ offer, onRequest, isRequesting }: TutorCardProps) {
  return (
    <Card className="h-full flex flex-col">
      <CardContent className="p-5 flex-1">
        <div className="flex items-start justify-between mb-4">
          <div className="flex items-center">
            <Avatar
              name={offer.tutorName}
              src={offer.tutorAvatar}
              size="md"
              className="mr-3" />

            <div>
              <h3 className="font-bold text-slate-900">{offer.tutorName}</h3>
              <div className="flex items-center text-xs text-slate-500">
                <Star className="w-3 h-3 text-amber-400 fill-amber-400 mr-1" />
                <span>Top Tutor</span>
              </div>
            </div>
          </div>
          <Badge variant="info">{offer.subjectName}</Badge>
        </div>

        <p className="text-sm text-slate-600 mb-4 line-clamp-3">
          {offer.description}
        </p>

        <div className="space-y-2 text-xs text-slate-500">
          <div className="flex items-center">
            <Clock className="w-3.5 h-3.5 mr-2 text-slate-400" />
            <span>{offer.availability}</span>
          </div>
          <div className="flex items-center">
            {offer.mode === 'online' &&
            <Monitor className="w-3.5 h-3.5 mr-2 text-slate-400" />
            }
            {offer.mode === 'onsite' &&
            <MapPin className="w-3.5 h-3.5 mr-2 text-slate-400" />
            }
            {offer.mode === 'both' &&
            <div className="flex mr-2">
                <Monitor className="w-3.5 h-3.5 mr-1 text-slate-400" />
                <MapPin className="w-3.5 h-3.5 text-slate-400" />
              </div>
            }
            <span className="capitalize">
              {offer.mode === 'both' ? 'Online & On-site' : offer.mode}
            </span>
          </div>
        </div>
      </CardContent>

      <CardFooter className="p-4 bg-slate-50">
        <Button
          variant="outline"
          className="w-full border-teal-200 text-teal-700 hover:bg-teal-50 hover:border-teal-300"
          onClick={() => onRequest(offer)}
          isLoading={isRequesting}
          disabled={isRequesting}>

          Request Session
        </Button>
      </CardFooter>
    </Card>);

}