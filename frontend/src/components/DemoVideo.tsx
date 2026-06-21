import { useState } from "react";
import {
  getDemoVideoUrl,
  isDemoVideoConfigured,
  isLocalVideoPath,
  toYouTubeEmbedUrl,
} from "../utils/demoConfig";

export default function DemoVideo() {
  const [hidden, setHidden] = useState(false);
  const demoVideoUrl = getDemoVideoUrl();

  if (!demoVideoUrl || hidden) {
    return null;
  }

  const youtubeEmbed = toYouTubeEmbedUrl(demoVideoUrl);

  if (!youtubeEmbed && !isLocalVideoPath(demoVideoUrl)) {
    return null;
  }

  return (
    <div className="w-full">
      <p className="text-sm font-medium text-slate-200 mb-2">Demo walkthrough</p>
      {youtubeEmbed ? (
        <div className="aspect-video w-full overflow-hidden rounded-lg border border-slate-800 bg-black">
          <iframe
            src={youtubeEmbed}
            title="Demo walkthrough"
            className="h-full w-full"
            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
            allowFullScreen
          />
        </div>
      ) : (
        <video
          controls
          playsInline
          preload="metadata"
          className="w-full rounded-lg border border-slate-800 bg-black aspect-video"
          onError={() => setHidden(true)}
        >
          <source src={demoVideoUrl} type="video/mp4" />
        </video>
      )}
    </div>
  );
}

export function hasDemoVideo(): boolean {
  return isDemoVideoConfigured();
}
